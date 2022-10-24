package management.controllers

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import management.data.docs.Document
import management.services.FillDocumentService


@Controller("/docs")
class DocumentController (private val fillDocumentService: FillDocumentService) {


    @Get("/export/default")
    fun exportDefaultDocs() : List<Document> {
        return fillDocumentService.exportDefaultDocs()
    }

}