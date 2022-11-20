package management.services

import jakarta.inject.Singleton
import management.data.products.AccompanyingDoc
import management.data.products.Product
import management.data.repositories.AccompanyingDocRepository
import management.data.repositories.ProductRepository
import management.data.utils.UpdateProduct
import management.forms.AccompanyingDocDto
import management.forms.ProductDto
import java.math.BigDecimal


@Singleton
class ProductService(
    private val productRepository: ProductRepository,
    private val accompanyingDocRepository: AccompanyingDocRepository
) {

    fun makeProducts(products: List<ProductDto>): MutableList<Product> {
        val productList: MutableList<Product> = mutableListOf()

        if (products.isEmpty())
            throw IllegalStateException("Not enough data in request")

        products.forEach { product ->
            productList.add(
                productRepository.findByAlias(product.alias)
                    ?: makeProduct(product)
            )
        }
        return productList
    }

    private fun makeProduct(product: ProductDto): Product {
        return Product(
            alias = product.alias,
            name = product.name,
            comment = product.comment ?: "",
            price = product.price, // "price":123.4
            tax = product.tax ?: BigDecimal.ZERO,
            currency = product.currency ?: "",
            units = product.units ?: "", // FIXME
            roundTotal = product.roundTotal ?: false, // "round_total":true
            dualDocs = product.dualDocs ?: false, // "dual_docs":true
            accompanyingDocs = makeAccompanyingDocs(product.accompanyingDocs ?: listOf())
        )
    }

    fun makeAccompanyingDoc(doc: AccompanyingDocDto): AccompanyingDoc {
        return accompanyingDocRepository.findByPath(doc.path)
            ?: AccompanyingDoc(
                path = doc.path,
                name = doc.name,
                raw = doc.raw
            )
    }

    fun makeAccompanyingDocs(docs: List<AccompanyingDocDto>): List<AccompanyingDoc> {
        val accompanyingDocList: MutableList<AccompanyingDoc> = mutableListOf()
        if (docs.isEmpty()) return accompanyingDocList
        docs.forEach { doc ->
            accompanyingDocList.add(
                accompanyingDocRepository.findByPath(doc.path)
                    ?: this.makeAccompanyingDoc(doc)
            )
        }
        return accompanyingDocList
    }

    fun getAllProducts(): MutableList<Product> {
        return productRepository.findAll()
    }

    fun getProductByAlias(alias: String): Product? {
        return productRepository.findByAlias(alias)
    }

    fun createProduct(productData: ProductDto): Product {
        return productRepository.save(makeProduct(productData))
    }

    fun createProducts(products: List<ProductDto>): MutableList<Product> {
        return productRepository.saveAll(makeProducts(products))
    }

    fun updateProductName(alias: String, requestData: UpdateProduct): Product {
        val product = productRepository.findByAlias(alias)
            ?: throw IllegalStateException("Cannot find product with alias $alias")
        product.name = requestData.name
            ?: throw IllegalStateException("No data in request")
        return productRepository.update(product)
    }

    fun updateProductComment(alias: String, requestData: UpdateProduct): Product {
        val product = productRepository.findByAlias(alias)
            ?: throw IllegalStateException("Cannot find product with alias $alias")
        product.comment = requestData.comment
            ?: throw IllegalStateException("No data in request")
        return productRepository.update(product)
    }


    fun updateProductPrice(alias: String, requestData: UpdateProduct): Product {
        val product = productRepository.findByAlias(alias)
            ?: throw IllegalStateException("Cannot find product with alias $alias")
        product.price = requestData.price
            ?: throw IllegalStateException("No data in request")
        return productRepository.update(product)
    }

    fun updateProductTax(alias: String, requestData: UpdateProduct): Product {
        val product = productRepository.findByAlias(alias)
            ?: throw IllegalStateException("Cannot find product with alias $alias")
        product.tax = requestData.tax
            ?: throw IllegalStateException("No data in request")
        return productRepository.update(product)
    }

    fun updateProductDualDocs(alias: String, requestData: UpdateProduct): Product {
        val product = productRepository.findByAlias(alias)
            ?: throw IllegalStateException("Cannot find product with alias $alias")
        product.dualDocs = requestData.dualDocs
            ?: throw IllegalStateException("No data in request")
        return productRepository.update(product)
    }

    fun updateProductRoundTotal(alias: String, requestData: UpdateProduct) : Product {
        val product = productRepository.findByAlias(alias)
            ?: throw IllegalStateException("Cannot find product with alias $alias")
        product.dualDocs = requestData.roundTotal
            ?: throw IllegalStateException("No data in request")
        return productRepository.update(product)
    }
    fun updateProductDocs(alias: String, docs: List<AccompanyingDocDto>): Product? {
        val product = productRepository.findByAlias(alias) ?: throw (Exception("No such product in database"))
        product.accompanyingDocs = makeAccompanyingDocs(docs)
        return productRepository.update(product)
    }

    fun addProductDocs(alias: String, docs: List<AccompanyingDocDto>): Product? {
        val product = productRepository.findByAlias(alias) ?: throw (Exception("No such product in database"))
        product.accompanyingDocs += makeAccompanyingDocs(docs)
        return productRepository.update(product)
    }

    fun deleteProduct(alias: String) {
        return productRepository.deleteByAlias(alias)
    }
}