package management.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.server.types.files.SystemFile
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import management.data.docs.Document
import management.data.docs.RenderedDocument
import management.forms.DocumentDto
import management.services.FillDocumentService
import management.services.PartnerService
import management.services.ProductService
import management.services.SolutionService
import management.utils.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Controller("/docs")
class DocumentController(
    private val fillDocumentService: FillDocumentService,
    private val productService: ProductService,
    private val solutionService: SolutionService,
    private val partnerService: PartnerService

) {

    private val documentsDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-yyyy")

    @Get("product/{alias}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getDocForProduct(@PathVariable alias: String, @QueryValue(defaultValue = "1") count: Short): SystemFile {
        val product = productService.getProductByAlias(alias) ?: /*return notFound()*/ throw IllegalStateException()
        val renderedDocuments = fillDocumentService.fillProductsDocuments(listOf(product), count)
        val productDocument = renderedDocuments.firstOrNull() ?: /*return notFound()*/  throw IllegalStateException()
        return if (renderedDocuments.count() > 1) {
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
    ): SystemFile {


        val solution = solutionService.getSolutionByAlias(solutionName) ?: solutionService.getSolutionByAlias("smart")!!
//        val partnerForm = partnerService.getFormByUNP(partnerUnp ?: 193141246)!!

        val renderedDocuments = fillDocumentService.step3Common(
            period = period,
            count = count,
            fullData = null,
            solution = solution
        )

        renderedDocuments.add(
            fillDocumentService.renderDocumentFromMap(
                "docs/fill_manual",
                fillDocumentService.getDocumentByAlias("skko_contract")!!,
                mapOf("SOLUTION" to solution.legalName)
            )
        )

        renderedDocuments.add(
            fillDocumentService.renderDocumentFromMap(
                "docs/fill_manual",
                fillDocumentService.getDocumentByAlias("skko_contract_application")!!,
                mapOf("SOLUTION" to solution.legalName)
            )
        )

        renderedDocuments.add(
            fillDocumentService.renderDocumentFromMap(
                "docs/fill_manual",
                fillDocumentService.getDocumentByAlias("sko_act")!!,
                mapOf("SOLUTION" to solution.legalName)
            )
        )

        renderedDocuments.add(
            fillDocumentService.renderDocumentFromMap(
                "docs/fill_manual",
                fillDocumentService.getDocumentByAlias("skko_connection_application")!!,
                mapOf("SOLUTION" to solution.legalName, "VERSION" to solution.version)
            )
        )

        renderedDocuments.add(
            fillDocumentService.renderDocument(
                "docs/fill_manual",
                fillDocumentService.getDocumentByAlias("connection_notification")!!
            )
        )

        renderedDocuments.add(
            fillDocumentService.renderDocument(
                "docs/fill_manual",
                fillDocumentService.getDocumentByAlias("declaration_lk_unsafe")!!
            )
        )

        if (solution.forcedInstructionPdf != null) {
            renderedDocuments.add(
                fillDocumentService.renderDocument(
                    "docs/",
                    solution.forcedInstructionPdf!!
                ).copy(extension = "pdf")
            )
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
    fun step3Post(@Body documentInfo: DocumentDto): SystemFile {
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

        val renderedDocument = fillDocumentService.step3Common(period, count, documentInfo, solution)

        if (documentInfo.contractData.organizationInfo.skkoNumber.isEmpty()) {
            renderedDocument.add(fillDocumentService.fillNewContract(documentInfo))
        } else {
            renderedDocument.add(fillDocumentService.fillExistingContract(documentInfo))
        }

        renderedDocument.add(
            fillDocumentService.fillSkoAct(
                documentInfo.contractData.organizationInfo, count
            )
        )

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
        val archiveName =
            "Заполненные документы для ${solution.name} от ${LocalDate.now().format(documentsDateFormat)}.zip"
        return serveFile(archive, archiveName)

    }

    enum class IkassaBillingMode {
        FULL,
        LICENSE,
        REGISTER
    }

}