package management.controllers

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.server.types.files.SystemFile
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import management.forms.DocumentDto
import management.services.FillDocumentService
import management.services.ProductService
import management.services.SolutionService
import management.utils.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Controller("/docs")
class DocumentController(
    private val fillDocumentService: FillDocumentService,
    private val productService: ProductService,
    private val solutionService: SolutionService

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
        val renderedDocuments = fillDocumentService.step3Common(
            period = period,
            count = count,
            fullData = null,
            solution = solution
        )
        val manualPath = "docs/fill_manual"

        renderedDocuments.add(
            fillDocumentService.renderDocumentFromMap(
                manualPath,
                fillDocumentService.getDocumentByAlias("skko_contract")!!,
                mapOf("SOLUTION" to solution.legalName)
            )
        )

        renderedDocuments.add(
            fillDocumentService.renderDocumentFromMap(
                manualPath,
                fillDocumentService.getDocumentByAlias("skko_contract_application")!!,
                mapOf("SOLUTION" to solution.legalName)
            )
        )

        renderedDocuments.add(
            fillDocumentService.renderDocumentFromMap(
                manualPath,
                fillDocumentService.getDocumentByAlias("sko_act")!!,
                mapOf("SOLUTION" to solution.legalName)
            )
        )

        renderedDocuments.add(
            fillDocumentService.renderDocumentFromMap(
                manualPath,
                fillDocumentService.getDocumentByAlias("skko_connection_application")!!,
                mapOf("SOLUTION" to solution.legalName, "VERSION" to solution.version)
            )
        )

        renderedDocuments.add(
            fillDocumentService.renderDocument(
                manualPath,
                fillDocumentService.getDocumentByAlias("connection_notification")!!
            )
        )

        renderedDocuments.add(
            fillDocumentService.renderDocument(
                manualPath,
                fillDocumentService.getDocumentByAlias("declaration_lk_unsafe")!!
            )
        )

        renderedDocuments.add(fillDocumentService.fillInstruction(null, solution))

        return serveFile(
            fillDocumentService.createZipArchive(renderedDocuments),
            "Документы к заполнению для ${solution.name} от ${LocalDate.now().format(documentsDateFormat)}.zip"
        )

    }

    @Post("/step3")
    @Produces("application/zip")
    @ExecuteOn(TaskExecutors.IO)
    fun step3Post(@Body documentInfo: DocumentDto): SystemFile {

        val solution = solutionService.getSolutionByAlias(documentInfo.equipment["solution"].toString())
            ?: solutionService.getSolutionByAlias("smart")!!
        val count = documentInfo.equipment.getOrDefault("units", "1").toShort()
        val period = documentInfo.equipment.getOrDefault("period", "1").toShort()
        for ((key, value) in documentInfo.contractData.bankInfo) {
            if (value.isBlank()) {
                documentInfo.contractData.bankInfo[key] = EMPTY_FIELD
            }
        }

        val renderedDocuments = fillDocumentService.step3Common(period, count, documentInfo, solution)

        if (documentInfo.contractData.organizationInfo.skkoNumber.isEmpty()) {
            renderedDocuments.add(fillDocumentService.fillNewContract(documentInfo))
        } else {
            renderedDocuments.add(fillDocumentService.fillExistingContract(documentInfo))
        }

        renderedDocuments.add(
            fillDocumentService.fillSkoAct(
                documentInfo.contractData.organizationInfo, count
            )
        )

        renderedDocuments.add(fillDocumentService.fillApplication(documentInfo))

        renderedDocuments.add(fillDocumentService.fillNotification(documentInfo.contractData.organizationInfo))

        renderedDocuments.add(fillDocumentService.fillLkUnsafe(documentInfo.contractData.organizationInfo))

        renderedDocuments.add(fillDocumentService.fillInstruction(null, solution))

        val archive = fillDocumentService.createZipArchive(renderedDocuments)
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