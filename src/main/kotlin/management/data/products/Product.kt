package management.data.products

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import management.forms.ProductPropertiesDto
import management.utils.ConstVariables.SCHEMA
import management.utils.asWords
import management.utils.toFixed
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import org.hibernate.annotations.Type
import java.math.BigDecimal
import javax.persistence.*


@Entity
@Table(name = "product", schema = SCHEMA)
@org.hibernate.annotations.TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
data class Product(

    @Column(name = "alias")
    var alias: String,

    @Column(name = "name")
    var name: String,

    @Column(name = "comment")
    var comment: String = "",

    @TypeDef(type = DataType.BIGDECIMAL)
    @Column(name = "price")
    var price: BigDecimal,

    @TypeDef(type = DataType.BIGDECIMAL)
    @Column(name = "tax")
    var tax: BigDecimal = BigDecimal.ZERO,

    @Column(name = "currency")
    val currency: String = "",

    @Column(name = "units")
    val units: String = "",

    @Column(name = "round_total")
    val roundTotal: Boolean = false,

    @Column(name = "dual_docs")
    var dualDocs: Boolean = false,

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(
        targetEntity = AccompanyingDoc::class,
        cascade = [CascadeType.ALL]
    )
    @JoinTable(
        name = "products_accompanying_docs",
        schema = SCHEMA,
        joinColumns = [JoinColumn(name = "product_id")],
        inverseJoinColumns = [JoinColumn(name = "accompanying_doc_id")]
    )
    var accompanyingDocs: List<AccompanyingDoc> = listOf(),

    @Column(name = "properties")
    @Type(type = "jsonb")
    val properties: ProductPropertiesDto = ProductPropertiesDto()

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    var productId: Long = 0


    fun toTotal(quantity: Short = 1): ProductTotal {
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
            priceFormatted = "$price $currency",
            quantity = quantity,
            quantityFormatted = "$quantity ${this.currency}",
            cost = cost,
            costFormatted = "$cost $currency",
            tax = tax,
            taxFormatted = if (tax == BigDecimal.ZERO) {
                "без НДС *"
            } else {
                "$tax"
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