package management.entities

import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import java.math.BigDecimal
import javax.persistence.*
import management.entities.AccompanyingDoc
import management.utils.ConstVariables.SCHEMA



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

    @ManyToMany(
        targetEntity = AccompanyingDoc::class,
        fetch = FetchType.EAGER,
        cascade = [(CascadeType.ALL)])
    @JoinTable(
        name = "products_accompanying_docs",
        schema = SCHEMA,
        joinColumns = [JoinColumn(name = "product_id")],
        inverseJoinColumns = [JoinColumn(name = "accompanying_doc_id")]
    )
    var accompanyingDocs: MutableList<AccompanyingDoc> = mutableListOf()


) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    var productId: Long = 0

}
