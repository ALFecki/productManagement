package management.data.repositories

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import management.data.products.Product


@Repository
interface ProductRepository : JpaRepository<Product, Long> {

    fun findByAlias(alias: String): Product?

    fun deleteByAlias(alias: String)

}