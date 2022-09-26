package management.entities

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.data.annotation.Query
import jakarta.persistence.*
import management.repositories.AccompanyingDocRepository
import reactor.core.publisher.Flux
import java.math.BigDecimal


@Entity
@Table(name = "Product", schema = "ikassa")
public class Product(

    @GeneratedValue
    @Id val productId: Int? = null,

    @Column(name = "alias")
    val alias: String? = null,

    @Column(name = "name")
    val name: String,

    @Column(name = "comment")
    val comment: String = "",

    @Column(name = "price")
    val price: BigDecimal,


//    @Column(name = "accompanying_docs")
//    @OneToMany(fetch = FetchType.LAZY,
//    mappedBy = "product")

//    @OneToMany(orphanRemoval = true)
//    @JoinTable(name = "ProductAccDocLink",
//        joinColumns = [JoinColumn(name = "product_link", referencedColumnName = "product_id")],
//        inverseJoinColumns = [JoinColumn(name = "accompanying_link", referencedColumnName = "acc_doc_id")])
//    val accompanyingDocs: Array<AccompanyingDoc>? = emptyArray(),


    @Column(name = "soft_relations")
    val softRelations: Array<Short>? = emptyArray(),

    @Column(name = "hard_relations")
    val hardRelations: Array<Short>? = emptyArray(),


//    val additionalFields: Map<String, String>? = mapOf(),


    @Column(name = "tax")
    val tax: BigDecimal = BigDecimal.ZERO,

    @Column(name = "currency")
    val currency: String = "",

    @Column(name = "units")
    val units: Int = 1,

    @Column(name = "round_total")
    val roundTotal: Boolean = false,

    @Column(name = "dual_docs")
    val dualDocs: Boolean = false

) {
//        val repository : AccompanyingDocRepository;
}
//    val additionalFields : Int =  0
//    @Query("SELECT * FROM ")
//    fun getAccompanyingDocs() {
//
//    }

