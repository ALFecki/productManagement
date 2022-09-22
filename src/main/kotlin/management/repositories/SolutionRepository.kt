package management.repositories

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactiveStreamsCrudRepository
import management.entities.Solution
import reactor.core.publisher.Flux


@R2dbcRepository(dialect = Dialect.POSTGRES)
interface SolutionRepository : ReactiveStreamsCrudRepository<Solution, Int> {

    fun findBySolutionId(solutionId : Int) : Solution
    fun findByName(name: String) : Solution
    override fun findAll() : Flux<Solution>

    fun save(entity: Solution) : Solution

}