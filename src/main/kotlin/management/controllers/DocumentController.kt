package management.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import management.data.docs.Document
import management.data.docs.RenderedDocument
import management.data.products.PartnerForm
import management.data.products.Solution
import management.forms.DocumentDto
import management.services.FillDocumentService
import management.services.PartnerService
import management.services.ProductService
import management.services.SolutionService
import management.utils.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Controller("/docs")
class DocumentController (
    private val fillDocumentService: FillDocumentService,
    private val productService: ProductService,
    private val solutionService: SolutionService,
    private val partnerService: PartnerService

    ) {

    private val documentsDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-yyyy")


    fun step3Common(
        period : Short,
        count: Short,
        fullData : DocumentDto? = null,
        solution : Solution = solutionService.getSolutionByAlias("smart")!!,
        partnerForm: PartnerForm = partnerService.getFormByUNP(193141246)!!
//        partnerUNP: Int? = null
    ) : MutableList<RenderedDocument> {


        val equipment = productService.getAllProducts().mapNotNull {
            val isProduct = solution.equipment.contains(productService.getProductByAlias(it.alias!!))
            if(
                isProduct &&
                period >= 6.toShort() &&
                listOf("pax930", "pax930_lancard", "pax930_promo").contains(it.alias)
            ) {
                it.copy(price=productService.getProductByAlias("pax930_promo")!!.price)
            } else if(isProduct) {
                it
            } else {
                null
            }
        }

        val solutionContent = solution.contents.map { it.alias }
        val renderedDocuments = mutableListOf<RenderedDocument>()

        val billingMode = if (solutionContent.isEmpty() || solutionContent.containsAll(listOf("ikassa_register", "ikassa_license"))) {
            IkassaBillingMode.FULL
        } else if (solutionContent.contains("ikassa_register")) {
            IkassaBillingMode.REGISTER
        } else {
            IkassaBillingMode.LICENSE
        }

        solution.contents.forEach {
            when(it.alias) { "ikassa_register" -> {
                when(billingMode) { IkassaBillingMode.FULL -> {
                        val ikassaInvoice = fillDocumentService.fillIkassaInvoice(
                            count,
                            period,
                            if (solutionContent.contains("dusik_r")) {
                                "_dusik"
                            }
                            else {
                                ""
                            }
                        )
                        ikassaInvoice.name = "${ikassaInvoice.name} за $period месяц${period.morph("", "а", "ев")}"
                        renderedDocuments.add(ikassaInvoice)
                    }

                    IkassaBillingMode.LICENSE -> {
                        val ikassaInvoice = fillDocumentService.fillIkassaRegistration(count)
                        renderedDocuments.add(ikassaInvoice)
                    }

                    IkassaBillingMode.REGISTER -> {
                        val ikassaInvoice = fillDocumentService.fillIkassaTariff(count, period)
                        ikassaInvoice.name = "${ikassaInvoice.name} за $period месяц${period.morph("", "а", "ев")}"
                        renderedDocuments.add(ikassaInvoice)
                    }

                }


            }
                "ikassa_license_12_season" -> {
                val licenseProduct = productService.getProductByAlias(it.alias!!)!!
                val ikassaInvoice = fillDocumentService.fillIkassaTariff(licenseProduct, count)
                ikassaInvoice.name = "${ikassaInvoice.name} за $period месяц${period.morph("", "а", "ев")}"
                renderedDocuments.add(ikassaInvoice)
            }
                "dusik_r" -> {

                }

                "skko_register" -> {
                    renderedDocuments.add(fillDocumentService.fillSkkoInvoice(count))
                }

            }
        }


        renderedDocuments.add(fillDocumentService.renderDocument("docs",
            fillDocumentService.getDocumentByAlias("attorney")!!
        ))
        renderedDocuments.addAll(
            fillDocumentService.fillProductsDocuments(equipment, count, fullData, solution)
        )
        return renderedDocuments
    }

    @Get("product/{alias}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getDocForProduct(@PathVariable alias : String, @QueryValue(defaultValue = "1") count : Short) : HttpResponse<*> {
        val product = productService.getProductByAlias(alias) ?: return notFound()
        val renderedDocuments = fillDocumentService.fillProductsDocuments(listOf(product), count)
        val productDocument = renderedDocuments.firstOrNull() ?: return notFound()
        return if(renderedDocuments.count() > 1) {
            serveFile(
                fillDocumentService.createZipArchive(renderedDocuments),
                "Документы для ${product.name.ellipsis(50)} от ${LocalDate.now().format(documentsDateFormat)}.zip"
            )
        } else {
            serveFile(
                productDocument.content,
                "${productDocument.name.ellipsis(50)} от ${
                    LocalDate.now().format(documentsDateFormat)
                }.${productDocument.extension}"
            )
        }
    }

    @Get("/step3")
    @Produces("application/zip")
    @ExecuteOn(TaskExecutors.IO)
    fun step3Get(
        @QueryValue(defaultValue = "1") count: Short,
        @QueryValue(defaultValue = "1") period: Short,
        @QueryValue(defaultValue = "smart") solutionName: String,
        @QueryValue(defaultValue = "") partnerUnp: Int? = null
    ) : HttpResponse<*> {


        val solution = solutionService.getSolutionByAlias(solutionName) ?: solutionService.getSolutionByAlias("smart")!!
        val partnerForm = partnerService.getFormByUNP(partnerUnp ?: 193141246)!!

        val renderedDocuments = this.step3Common(
            period = period,
            count = count,
            fullData = null,
            solution = solution,
            partnerForm = partnerForm)

        renderedDocuments.add(fillDocumentService.renderDocumentFromMap(
            "docs/fill_manual",
            fillDocumentService.getDocumentByAlias("skko_contract")!!,
            mapOf("SOLUTION" to solution.legalName)
        ))

        renderedDocuments.add(fillDocumentService.renderDocumentFromMap(
            "docs/fill_manual",
            fillDocumentService.getDocumentByAlias("skko_contract_application")!!,
            mapOf("SOLUTION" to solution.legalName)
        ))

        renderedDocuments.add(fillDocumentService.renderDocumentFromMap(
            "docs/fill_manual",
            fillDocumentService.getDocumentByAlias("sko_act")!!,
            mapOf("SOLUTION" to solution.legalName)
        ))

        renderedDocuments.add(fillDocumentService.renderDocumentFromMap(
            "docs/fill_manual",
            fillDocumentService.getDocumentByAlias("skko_connection_application")!!,
            mapOf("SOLUTION" to solution.legalName, "VERSION" to solution.version)
        ))

        renderedDocuments.add(fillDocumentService.renderDocument(
            "docs/fill_manual",
            fillDocumentService.getDocumentByAlias("connection_notification")!!
        ))

        renderedDocuments.add(fillDocumentService.renderDocument(
            "docs/fill_manual",
            fillDocumentService.getDocumentByAlias("declaration_lk_unsafe")!!
        ))

        if(solution.forcedInstructionPdf != null) {
            renderedDocuments.add(fillDocumentService.renderDocument(
                "docs/",
                solution.forcedInstructionPdf!!
            ).copy(extension = "pdf"))
        } else {
            val instruction = fillDocumentService.fillInstruction(null, solution)
            renderedDocuments.add(
                RenderedDocument(
                    "00-Инструкция",
                    instruction.replace("width=\"600\"", "width=\"800\"").toPDF(),
                    "pdf"
                )
            )
        }


        return serveFile(
            fillDocumentService.createZipArchive(renderedDocuments),
            "Документы к заполнению для ${solution.name} от ${LocalDate.now().format(documentsDateFormat)}.zip"
        )

    }

    @Post("/step3")
    @Produces("application/zip")
    @ExecuteOn(TaskExecutors.IO)
    fun step3Post(@Body documentInfo : DocumentDto) : HttpResponse<*> {
        println("STEP3")
//        val form  = partnerService.getFormByUNP(documentInfo.equipment["partner_unp"]!!.toInt())
//            ?: partnerService.

        val solution = solutionService.getSolutionByAlias(documentInfo.equipment["solution"].toString())
            ?: solutionService.getSolutionByAlias("smart")!!
        val count = documentInfo.equipment.getOrDefault("units", "1").toShort()
        val period = documentInfo.equipment.getOrDefault("period", "1").toShort()
        for ((key, value) in documentInfo.contractData.bankInfo) {
            if (value.isBlank()) {
                documentInfo.contractData.bankInfo[key] = EMPTY_FIELD
            }
        }

        val renderedDocument = this.step3Common(period, count, documentInfo, solution)

        if(documentInfo.contractData.organizationInfo.skkoNumber.isEmpty()) {
            renderedDocument.add(fillDocumentService.fillNewContract(documentInfo))
        } else {
            renderedDocument.add(fillDocumentService.fillExistingContract(documentInfo))
        }

        renderedDocument.add(fillDocumentService.fillSkoAct(documentInfo.contractData.organizationInfo, count))
        renderedDocument.add(fillDocumentService.fillApplication(documentInfo))

        renderedDocument.add(fillDocumentService.fillNotification(documentInfo.contractData.organizationInfo))

        renderedDocument.add(fillDocumentService.fillLkUnsafe(documentInfo.contractData.organizationInfo))

        val instruction = if (solution.forcedInstructionPdf != null) {
            renderedDocument.add(
            fillDocumentService
                .renderDocument("docs/", solution.forcedInstructionPdf!!)
                .copy(extension = "pdf")
            )
            "Перечень документов, которые необходимо предоставить для регистрации, находится в прикрепленном файле."
        } else {
            val instruction = fillDocumentService.fillInstruction(null, solution)
            renderedDocument.add(
                RenderedDocument(
                    "00-Инструкция",
                    instruction
                        .replace("width=\"600\"", "width=\"800\"").toPDF(),
                    "pdf"
                )
            )
            instruction
        }
        val archive = fillDocumentService.createZipArchive(renderedDocument)
        val archiveName = "Заполненные документы для ${solution.name} от ${LocalDate.now().format(documentsDateFormat)}.zip"
        return serveFile(archive, archiveName)

    }


    @Get("/export/default")
    fun exportDefaultDocs() : HttpResponse<*> {
        val products = productService.exportDefault()
        val solutions = solutionService.exportDefaultSolutions()
        val docs = fillDocumentService.exportDefaultDocs()
        val forms = partnerService.exportDefault()
        val utils = fillDocumentService.exportDefaultUtils()
        if (products.isNotEmpty() && solutions.isNotEmpty() && docs.isNotEmpty()
            && forms.isNotEmpty() && utils.isNotEmpty()) {
            return HttpResponse.ok<Document>()
        }
        return notFound()
    }

    enum class IkassaBillingMode {
        FULL,
        LICENSE,
        REGISTER
    }

}