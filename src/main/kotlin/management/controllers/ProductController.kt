package management.controllers

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonArray
import io.micronaut.json.tree.JsonNode
import management.entities.Product
import management.services.ProductService


@Controller("/products")
class ProductController(private val productService: ProductService) {


    @Get
    fun getAllProducts(): MutableList<Product> {
        return productService.getAllProducts()
    }

    @Get("/{alias}")
    fun getProductByAlias(@PathVariable alias : String) : Product? {
        return productService.getProductByAlias(alias)
    }

    @Get("/export/default")
    fun exportDefaultProducts() : List<Product> {
        return productService.exportDefault()
    }

    @Get("/delete/{alias}")
    fun deleteProduct(@PathVariable alias: String) {
        return productService.deleteProduct(alias)
    }

    @Post("/create")
    fun createProduct(@Body requestData : JsonArray) : MutableList<Product>? {
        return productService.createProduct(requestData)
    }

    @Post("/update/name/{alias}")
    fun updateProductName(@PathVariable alias: String, @Body name : Map<String, String>) {
        return productService.updateProductName(alias, name)
    }

    @Post("/update/price/{alias}")
    fun updateProductPrice(@PathVariable alias: String, @Body price : Map<String, String>) {
        return productService.updateProductPrice(alias, price)
    }

    @Post("/update/tax/{alias}")
    fun updateProductTax(@PathVariable alias: String, @Body tax : Map<String, String>){
        return productService.updateProductTax(alias, tax)
    }



}