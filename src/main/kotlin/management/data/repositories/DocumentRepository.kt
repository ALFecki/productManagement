package management.data.repositories

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import management.data.docs.Document

@Repository
interface DocumentRepository : JpaRepository<Document, Long> {
    fun findByAlias(alias: String): Document?


    fun deleteByAlias(alias: String)
}