package management.repositories

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import management.entities.Solution


@Repository
interface SolutionRepository : JpaRepository<Solution, Long> {
    fun findByAlias(alias : String) : Solution
    fun deleteByAlias(alias : String)
}