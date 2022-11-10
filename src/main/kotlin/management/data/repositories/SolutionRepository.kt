package management.data.repositories

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import management.data.products.Solution
import java.math.BigDecimal


@Repository
interface SolutionRepository : JpaRepository<Solution, Long> {
    fun findByAlias(alias : String) : Solution?
    fun deleteByAlias(alias : String)
    fun updateByAlias(alias : String, name : String)
    fun updateByAlias(alias : String, price : BigDecimal)

}