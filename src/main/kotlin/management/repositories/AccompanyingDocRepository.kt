package management.repositories

import io.micronaut.data.annotation.Query
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactiveStreamsCrudRepository
import management.entities.AccompanyingDoc


@R2dbcRepository(dialect = Dialect.POSTGRES)
interface AccompanyingDocRepository : ReactiveStreamsCrudRepository<AccompanyingDoc, Int> {

    fun findByAccDocId(AccDocId : Int) : AccompanyingDoc
    fun findByName(name : String) : AccompanyingDoc

    fun save(entity: AccompanyingDoc)
    fun saveAll(entities: List<AccompanyingDoc>)

    fun updateByAccDocId( AccDocId: Int, entity: AccompanyingDoc)
    fun updateByName(name: String, entity: AccompanyingDoc)

    fun deleteByAccDocId(AccDocId: Int)
    fun deleteByName(name: String)

//    @Query("SELECT accompanying_id FROM \"ProductAccDocLink\" WHERE product_id = :productId")
//    fun find


}