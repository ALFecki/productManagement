package management.controllers

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Produces
import management.entities.AccompanyingDoc
import management.entities.Product
import management.repositories.AccompanyingDocRepository
import management.repositories.ProductRepository
import java.math.BigDecimal


@Controller("/products")
class ProductController (private val productRepository: ProductRepository,
                          ) {

    @Get
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllProducts() : MutableList<Product>? {
        return  productRepository.findAll()
            .collectList().block()
    }

    @Get("/insert")
    fun insertTest() {
//        val doc : AccompanyingDoc = AccompanyingDoc(name = "test", path = "default")

//        accompanyingDocRepository.save(doc)

        val product : Product = Product(alias = "soaijd", name = "asofas",
            price = BigDecimal.ONE)

        productRepository.save(product)

    }

    @Get("/name={name}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getProductByName(@PathVariable name : String) : MutableList<Product>? {
        return productRepository.findByName(name)
            .collectList().block()
    }

    @Get("/{alias}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getProductByAlias(@PathVariable alias : String) : MutableList<Product>? {
        return productRepository.findByAlias(alias)
            .collectList().block()
    }


}