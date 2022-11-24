package management.services

import jakarta.inject.Singleton
import management.data.docs.Document
import management.data.repositories.DocumentRepository
import management.forms.DocumentDto

@Singleton
class DocumentService(
    private val documentRepository: DocumentRepository
) {

    fun makeDocument(data: DocumentDto) : Document {
        return Document(
            alias = data.alias,
            name = data.name,
            path = data.path
        )
    }

    fun getAllDocuments() : List<Document>? {
        return documentRepository.findAll()
    }

    fun getDocumentByAlias(alias: String) : Document? {
        return documentRepository.findByAlias(alias)
    }

    fun createDocument(data: DocumentDto) : Document {
        return documentRepository.save(makeDocument(data))
    }

    fun deleteDocumentByAlias(alias: String) {
        return documentRepository.deleteByAlias(alias)
    }
}