package management.data.repositories

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import management.data.entities.AccompanyingDoc

@Repository
interface AccompanyingDocRepository : JpaRepository<AccompanyingDoc, Long> {
    fun findByPath(path : String) : AccompanyingDoc?
}