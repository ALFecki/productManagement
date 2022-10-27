package management.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import management.data.docs.Document
import management.data.products.PartnerForm
import management.forms.DocumentDto
import management.services.FillDocumentService
import management.services.PartnerService
import management.services.ProductService
import management.services.SolutionService
import management.utils.ellipsis
import management.utils.notFound
import management.utils.serveFile
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

    @Post("/step3")
    @Produces("application/zip")
    fun step3Post(@Body documentInfo : DocumentDto) : HttpResponse<*> {
//        val form  = partnerService.getFormByUNP(documentInfo.equipment["partner_unp"]!!.toInt())
//            ?: partnerService.

        val solution = solutionService.getSolutionByAlias(documentInfo.equipment["solution"].toString())
            ?: solutionService.getSolutionByAlias("smart")




        throw NotImplementedError("HOW ARE YOU?")
    }


    @Get("/export/default")
    fun exportDefaultDocs() : List<Document> {
        return fillDocumentService.exportDefaultDocs()
    }

}