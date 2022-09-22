package management.entities

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.data.annotation.Query
import jakarta.persistence.*
import management.repositories.AccompanyingDocRepository
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
        @OneToMany
        val accompanyingDocs: List<AccompanyingDoc>? = listOf<AccompanyingDoc>(),


//    @Column(name = "SoftRelations")
//    val softRelations: Array<Short>,

//    @Column(name = "HardRelations")
//    val hardRelations: List<Short>? = listOf(),


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

    )
{

}
//    val additionalFields : Int =  0
//    @Query("SELECT * FROM ")
//    fun getAccompanyingDocs() {
//
//    }

