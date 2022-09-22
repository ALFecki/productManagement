package management.repositories


import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import management.entities.Product
import io.micronaut.data.repository.reactive.ReactiveStreamsCrudRepository
import reactor.core.publisher.Flux

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface ProductRepository : ReactiveStreamsCrudRepository<Product, Int> {

        fun findByName(name: String) : Flux<Product>
        fun findByAlias(alias : String) : Flux<Product>
        override fun findAll() : Flux<Product>

        fun save(entity: Product) : Product
        fun saveAll(entities: List<Product>)

        fun updateByProductId(productId : Int, entity: Product)
        fun updateByName(name : String, entity: Product)

        fun deleteByProductId(productId: Int)
        fun deleteByName(name : String)
}
