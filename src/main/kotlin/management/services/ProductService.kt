package management.services

import io.micronaut.json.tree.JsonArray
import io.micronaut.json.tree.JsonNode
import jakarta.inject.Singleton
import management.entities.AccompanyingDoc
import management.entities.Product
import management.repositories.ProductRepository
import java.math.BigDecimal


@Singleton
class ProductService (private val productRepository: ProductRepository) {

    private fun makeProduct(productData: JsonArray) : MutableList<Product>? {
        val productList : MutableList<Product> = mutableListOf()
        (0 until productData.size()).forEach {
            val product : JsonNode = productData.get(it) ?: return null
            var docs : MutableList<AccompanyingDoc> = mutableListOf()
            if (product["accompanying_docs"] != null) {
                 docs = makeAccompanyingDocs(product.get("accompanying_docs") as JsonArray)
            }
            productList += Product(
                alias = product.get("alias")!!.stringValue,
                name = product.get("name")!!.stringValue,
                comment = product.get("comment")?.stringValue,
                price = product.get("price")!!.bigDecimalValue, // "price":123.4
                tax = product.get("tax")?.bigDecimalValue ?: BigDecimal.ZERO,
                currency = product.get("currency")?.stringValue,
                units = product.get("units")?.intValue ?: 1,
                roundTotal = product.get("round_total")?.booleanValue ?: false, // "round_total":true
                dualDocs = product.get("dual_docs")?.booleanValue ?: false, // "dual_docs":true
                accompanyingDocs = docs
            )
        }
        return productList
    }

    private fun makeAccompanyingDocs(docs : JsonArray) : MutableList<AccompanyingDoc> {
        val accompanyingDocList : MutableList<AccompanyingDoc> = mutableListOf()
        (0 until docs.size()).forEach {
            val doc : JsonNode = docs.get(it)!!
            accompanyingDocList += AccompanyingDoc(
                path = doc.get("path")!!.stringValue,
                name = doc.get("name")!!.stringValue,
                raw = doc.get("raw")?.booleanValue ?: false
            )
        }
        return accompanyingDocList
    }

    fun getAllProducts() : MutableList<Product> {
        return productRepository.findAll()
    }

    fun getProductByAlias(alias : String) : MutableList<Product> {
        return productRepository.findByAlias(alias)
    }

    fun createProduct(productData : JsonArray) : MutableList<Product> {
        return productRepository.saveAll(makeProduct(productData)!!)
    }

    fun updateProductName(alias: String, name : Map<String, String>) {
        return productRepository.updateByAlias(alias, name["name"]!!)
    }

    fun updateProductPrice(alias: String, price : Map<String, String>) {
        return productRepository.updateByAlias(alias, price["price"]!!.toBigDecimal())
    }

    fun updateProductTax(alias : String, tax : Map<String, String>) {
        return productRepository.updateByAlias(
            alias,
            tax.getOrDefault("tax", BigDecimal.ZERO)
            .toString()
            .toBigDecimal())
    }

    fun deleteProduct(alias : String) {
        return productRepository.deleteByAlias(alias)
    }

}