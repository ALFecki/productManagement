package management.links.repositories

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.operations.JpaRepositoryOperations
import io.micronaut.data.jpa.repository.JpaRepository
import jakarta.inject.Inject
import management.links.entities.ProductAccDoc
import javax.transaction.Transactional


@Repository
interface ProductAccDocRepository : JpaRepository<ProductAccDoc, Long> {

//    @Inject
//    val operations: JpaRepositoryOperations
//
//
//    @Query(nativeQuery = true, value = "INSERT INTO ikassa.products_accompanying_docs (product_id, accompanying_doc_id) VALUES ((SELECT product_id FROM ikassa.product WHERE alias = :alias), (SELECT accompanying_doc_id FROM ikassa.accompanying_doc WHERE path = :path))")
//    fun insertLink(alias : String, path : String)
//    @Transactional
//    fun insertLink(alias : String, path : String) {
//        JpaRepositoryOperations::getCurrentEntityManager
//            .createNativeQuery("INSERT INTO ikassa.products_accompanying_docs (product_id, accompanying_doc_id)" +
//                    " VALUES ((SELECT product_id FROM ikassa.product WHERE alias = :alias)," +
//                    " (SELECT accompanying_doc_id FROM ikassa.accompanying_doc WHERE path = :path))")
//            .setParameter("alias", alias)
//            .setParameter("path", path)
//            .executeUpdate()
//    }
}