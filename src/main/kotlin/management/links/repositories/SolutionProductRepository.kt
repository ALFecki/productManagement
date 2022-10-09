package management.links.repositories

import io.micronaut.data.jpa.repository.JpaRepository
import management.links.entities.ProductSolution

interface SolutionProductRepository : JpaRepository<ProductSolution, Long> {

}