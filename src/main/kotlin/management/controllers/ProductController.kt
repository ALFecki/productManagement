package management.controllers

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonArray
import io.micronaut.json.tree.JsonNode
import management.data.products.Product
import management.forms.ProductDto
import management.services.ProductService
import java.math.BigDecimal


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
    fun createProduct(@Body requestData : ProductDto) : MutableList<Product>? {
        return productService.createProduct(requestData)
    }

    @Post("/update/name/{alias}")
    fun updateProductName(@PathVariable alias: String, @Body name : Map<String, String>) : Product {
        return productService.updateProductName(alias, name)
    }

    @Post("/update/comment/{alias}")
    fun updateProductComment(@PathVariable alias: String, @Body comment : Map<String, String>) : Product {
        return productService.updateProductComment(alias, comment)
    }


    @Post("/update/price/{alias}")
    fun updateProductPrice(@PathVariable alias: String, @Body price : Map<String, BigDecimal>) : Product {
        return productService.updateProductPrice(alias, price)
    }

    @Post("/update/tax/{alias}")
    fun updateProductTax(@PathVariable alias: String, @Body tax : Map<String, BigDecimal>) : Product{
        return productService.updateProductTax(alias, tax)
    }

    @Post("/update/dual_docs/{alias}")
    fun updateProductDualDocs(@PathVariable alias: String, @Body dualDocs : Map<String, Boolean>) : Product {
        return productService.updateProductDualDocs(alias, dualDocs)
    }

    @Post("/update/docs/{alias}")
    fun updateAccompanyingDocs(@PathVariable alias : String, @Body docs : JsonNode) : Product? {
        return productService.updateProductDocs(alias, docs)
    }

    @Post("/add/docs/{alias}")
    fun addAccompanyingDocs(@PathVariable alias : String, @Body docs : JsonNode) : Product? {
        return productService.addProductDocs(alias, docs)
    }

}