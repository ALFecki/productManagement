package management.repositories

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import management.entities.Product
import java.math.BigDecimal


@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findByAlias(alias : String) : List<Product>
    fun deleteByAlias(alias: String)
    fun updateByAlias(alias: String, name : String)
    fun updateByAlias(alias: String, price : BigDecimal)
//    fun updateTaxByAlias(alias: String, tax : BigDecimal)
}