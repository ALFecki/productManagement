package management.repositories

import io.micronaut.data.jpa.repository.JpaRepository
import management.entities.AccompanyingDoc

interface AccompanyingDocRepository : JpaRepository<AccompanyingDoc, Long> {
    fun findByPath(path : String) : List<AccompanyingDoc>
}