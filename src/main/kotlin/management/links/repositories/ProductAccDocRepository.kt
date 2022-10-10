package management.links.repositories

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import management.links.entities.ProductAccDoc


@Repository
interface ProductAccDocRepository : JpaRepository<ProductAccDoc, Long> {

}