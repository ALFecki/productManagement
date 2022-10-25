package management.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.QueryValue
import management.data.docs.Document
import management.services.FillDocumentService
import management.services.ProductService
import management.utils.ellipsis
import management.utils.notFound
import management.utils.serveFile
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Controller("/docs")
class DocumentController (
    private val fillDocumentService: FillDocumentService,
    private val productService: ProductService

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


    @Get("/export/default")
    fun exportDefaultDocs() : List<Document> {
        return fillDocumentService.exportDefaultDocs()
    }

}