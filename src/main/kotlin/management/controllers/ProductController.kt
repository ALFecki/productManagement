package management.controllers

import com.fasterxml.jackson.annotation.JsonValue
import io.micronaut.http.annotation.*
import management.data.products.Product
import management.data.utils.UpdateProduct
import management.forms.AccompanyingDocDto
import management.forms.ProductDto
import management.services.ProductService
import org.hibernate.sql.Update
import java.math.BigDecimal


@Controller("/products")
class ProductController(private val productService: ProductService) {

    @Get
    fun getAllProducts(): MutableList<Product> {
        return productService.getAllProducts()
    }

    @Get("/{alias}")
    fun getProductByAlias(@PathVariable alias: String): Product? {
        return productService.getProductByAlias(alias)
    }

    @Get("/export/default")
    fun exportDefaultProducts(): List<Product> {
        return productService.exportDefault()
    }

    @Get("/delete/{alias}")
    fun deleteProduct(@PathVariable alias: String) {
        return productService.deleteProduct(alias)
    }

    @Post("/create")
    fun createProduct(@Body requestData: ProductDto): Product {
        return productService.createProduct(requestData)
    }

    @Post("/create/list")
    fun createProducts(@Body requestData: List<ProductDto>): MutableList<Product> {
        return productService.createProducts(requestData)
    }

    @Post("/update/name/{alias}")
    fun updateProductName(@PathVariable alias: String, @Body requestData: UpdateProduct): Product {
        return productService.updateProductName(alias, requestData)
    }

    @Post("/update/comment/{alias}")
    fun updateProductComment(@PathVariable alias: String, @Body requestData: UpdateProduct): Product {
        return productService.updateProductComment(alias, requestData)
    }


    @Post("/update/price/{alias}")
    fun updateProductPrice(@PathVariable alias: String, @Body requestData: UpdateProduct): Product {
        return productService.updateProductPrice(alias, requestData)
    }

    @Post("/update/tax/{alias}")
    fun updateProductTax(@PathVariable alias: String, @Body requestData: UpdateProduct): Product {
        return productService.updateProductTax(alias, requestData)
    }

    @Post("/update/dual_docs/{alias}")
    fun updateProductDualDocs(@PathVariable alias: String, @Body requestData: UpdateProduct): Product {
        return productService.updateProductDualDocs(alias, requestData)
    }

    @Post("/update/round_total/{alias}")
    fun updateProductRoundTotal(@PathVariable alias : String, @Body requestData: UpdateProduct) : Product {
        return productService.updateProductRoundTotal(alias, requestData)
    }

    @Post("/update/docs/{alias}")
    fun updateAccompanyingDocs(@PathVariable alias: String, @Body docs: List<AccompanyingDocDto>): Product? {
        return productService.updateProductDocs(alias, docs)
    }

    @Post("/add/docs/{alias}")
    fun addAccompanyingDocs(@PathVariable alias: String, @Body docs: List<AccompanyingDocDto>): Product? {
        return productService.addProductDocs(alias, docs)
    }

}