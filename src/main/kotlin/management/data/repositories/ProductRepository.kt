package management.data.repositories

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import management.data.products.Product


@Repository
interface ProductRepository : JpaRepository<Product, Long> {

    fun findByAlias(alias: String): Product?

    fun deleteByAlias(alias: String)

//    @Query("""FROM product WHERE """)
//    fun getProductsForEquipment(alias: String)
//    fun updateByAlias(alias: String, name : String)
//    fun updateByAlias(alias: String, price : BigDecimal)
//

}