package management.data.entities

import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import java.math.BigDecimal
import javax.persistence.*
import management.utils.ConstVariables.SCHEMA
import management.utils.asWords
import management.utils.toFixed
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption


@Entity
@Table(name = "product", schema = SCHEMA)
data class Product(

        @Column(name = "alias")
        var alias: String? = null,

        @Column(name = "name")
        var name: String,

        @Column(name = "comment")
        val comment: String? = null,

        @TypeDef(type = DataType.BIGDECIMAL)
        @Column(name = "price")
        val price: BigDecimal,

        @TypeDef(type = DataType.BIGDECIMAL)
        @Column(name = "tax")
        val tax: BigDecimal = BigDecimal.ZERO,

        @Column(name = "currency")
        val currency: String? = null,

        @Column(name = "units")
        val units: Int = 1,

        @Column(name = "round_total")
        val roundTotal: Boolean = false,

        @Column(name = "dual_docs")
        val dualDocs: Boolean = false,

        @LazyCollection(LazyCollectionOption.FALSE)
        @ManyToMany(
                targetEntity = AccompanyingDoc::class,
                cascade = [CascadeType.ALL])
        @JoinTable(
                name = "products_accompanying_docs",
                schema = SCHEMA,
                joinColumns = [JoinColumn(name = "product_id")],
                inverseJoinColumns = [JoinColumn(name = "accompanying_doc_id")]
        )
        var accompanyingDocs: List<AccompanyingDoc> = listOf()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    var productId: Long = 0


    fun toTotal(quantity : Short = 1) : ProductTotal {
        val taxSum = (tax.divide(BigDecimal(100)) * price).toFixed(2)
        val taxTotal = (taxSum * BigDecimal(quantity.toInt())).toFixed(2)
        val cost = (price + taxSum).toFixed(2)
        val subTotal = (price * BigDecimal(quantity.toInt())).toFixed(2)
        val total = if (roundTotal) {
               (cost * BigDecimal(quantity.toInt())).toFixed(0)
        } else {
               (cost * BigDecimal(quantity.toInt())).toFixed(2)
        }
        return ProductTotal(
                price = this.price,
                priceFormatted = "${price} ${currency}",
                quantity = quantity,
                quantityFormatted = "${quantity} ${this.currency}",
                cost = cost,
                costFormatted = "${cost} ${currency}",
                tax = tax,
                taxFormatted = if(tax == BigDecimal.ZERO) {
                        "без НДС *"
                } else {
                        "${tax}"
                },
                taxSum = taxSum,
                taxSumFormatted = "$taxSum $currency",
                taxTotal = taxTotal,
                taxTotalFormatted = "$taxTotal $currency",
                subTotal = subTotal,
                subTotalFormatted = "$subTotal $currency",
                total = total,
                totalFormatted = "$total $currency",
                totalText = total.asWords()
        )
    }

}