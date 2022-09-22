package management.repositories

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactiveStreamsCrudRepository
import management.entities.TestEntity

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface TestRepository : ReactiveStreamsCrudRepository<TestEntity, Int> {
        fun save(entity: TestEntity) : TestEntity

        fun update(entity: TestEntity) : TestEntity
}






/*


import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactiveStreamsCrudRepository
import management.entities.TestEntity
import io.r2dbc.spi.Statement
import io.r2dbc.spi.Connection

import org.reactivestreams.Publisher
import reactor.core.publisher.Mono

@R2dbcRepository(dialect = Dialect.POSTGRES)
class TestRepository : ReactiveStreamsCrudRepository<TestEntity, Int> {
//    private val connection : Connec

    override fun <S : TestEntity?> save(entity: S): Publisher<S> {
        TODO("Not yet implemented")
    }

    override fun <S : TestEntity?> saveAll(entities: MutableIterable<S>): Publisher<S> {
        TODO("Not yet implemented")
    }

    override fun <S : TestEntity?> update(entity: S): Publisher<S> {
        TODO("Not yet implemented")
    }

    override fun <S : TestEntity?> updateAll(entities: MutableIterable<S>): Publisher<S> {
        TODO("Not yet implemented")
    }

    override fun findById(id: Int): Publisher<TestEntity> {
        TODO("Not yet implemented")
    }

    override fun findAll(): Publisher<TestEntity> {
        TODO("Not yet implemented")
    }

    override fun existsById(id: Int): Publisher<Boolean> {
        TODO("Not yet implemented")
    }

    override fun count(): Publisher<Long> {
        TODO("Not yet implemented")
    }

    override fun delete(entity: TestEntity): Publisher<Long> {
        TODO("Not yet implemented")
    }

    override fun deleteAll(): Publisher<Long> {
        TODO("Not yet implemented")
    }

    override fun deleteAll(entities: MutableIterable<TestEntity>): Publisher<Long> {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int): Publisher<Long> {
        TODO("Not yet implemented")
    }


    fun save(parameter: TestEntity) {
        val statement : Statement = createStatement("INSERT INTO test(name) VALUES (:name)")
        statement.bind("name", parameter.name)
        statement.execute()
//        return Mono.from(statement.execute())


    }
//    override fun findAll(): Flux<testEntity>
//    override fun findById(id: @NotNull Int): Mono<testEntity>
}*/
