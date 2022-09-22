package management.controllers


import io.micronaut.data.repository.reactive.ReactiveStreamsCrudRepository
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Controller
import management.entities.Product
import management.entities.TestEntity
import management.repositories.ProductRepository
import management.repositories.TestRepository
import java.math.BigDecimal

@Controller("/")
class TestController(private val repository: ProductRepository) {

    @Get("test")
    fun testAnswer() {
        println("Hello, World!")
    }
    @Get("insert")
    fun insertTest() {
        println("Testing insert")
        val test = Product(alias = "something",
            name = "test", price = BigDecimal(2765), tax = BigDecimal(3874))
        repository.save(test)
        println("Insert success")
        val test1 = repository.findAll().collectList().block()
        println("Founded product is ${test1[1].price}, ${test1[2].name}")

    }

}