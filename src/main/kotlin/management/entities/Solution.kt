package management.entities

import java.math.BigDecimal
import javax.persistence.*
import management.utils.ConstVariables.SCHEMA


@Entity
@Table(name = "solution", schema = SCHEMA)
data class Solution(
        @Column(name = "alias")
        val alias : String,

        @Column (name = "name")
        val name : String,

        @ManyToMany(
                targetEntity = Product::class,
                fetch = FetchType.LAZY,
                cascade = [(CascadeType.ALL)]
        )
        @JoinTable(
                name = "solutions_products",
                schema = SCHEMA,
                joinColumns = [JoinColumn(name = "solution_id")],
                inverseJoinColumns = [JoinColumn(name = "product_id")]
        )
        var contents: MutableList<Product> = mutableListOf(),

        @ManyToMany(
                targetEntity = Product::class,
                fetch = FetchType.LAZY,
                cascade = [(CascadeType.ALL)]
        )
        @JoinTable(
                name = "solutions_related_products",
                schema = SCHEMA,
                joinColumns = [JoinColumn(name = "solution_id")],
                inverseJoinColumns = [JoinColumn(name = "product_id")]
        )
        var related: MutableList<Product> = mutableListOf(),

        @Column(name = "price")
        val price : BigDecimal? = null, // WHY?

        @ManyToMany(
                targetEntity = AccompanyingDoc::class,
                fetch = FetchType.LAZY,
                cascade = [(CascadeType.ALL)]
        )
        @JoinTable(
                name = "solutions_accompanying_docs",
                schema = SCHEMA,
                joinColumns = [JoinColumn(name = "solution_id")],
                inverseJoinColumns = [JoinColumn(name = "accompanying_doc_id")]
        )
        var accompanyingDoc: MutableList<AccompanyingDoc> = mutableListOf(),

        @ManyToMany(
                targetEntity = Product::class,
                fetch = FetchType.LAZY,
                cascade = [(CascadeType.ALL)]
        )
        @JoinTable(
                name = "solutions_equipment",
                schema = SCHEMA,
                joinColumns = [JoinColumn(name = "solution_id")],
                inverseJoinColumns = [JoinColumn(name = "product_id")]
        )
        var equipment: MutableList<Product> = mutableListOf(),

//    @TypeDef(type = DataType.JSON)
//    @Column(name = "extra_vars")
//    val extraVars : Map<String, String>,

        @Column(name ="legal_name")
        val legalName : String,

        @Column(name = "version")
        val version : String = "2.4.0",

        @OneToOne(
                targetEntity = AccompanyingDoc::class,
                fetch = FetchType.EAGER,
                cascade = [(CascadeType.ALL)]
        )
        @JoinColumn(name = "forced_instruction_link")
        val forcedInstructionPdf : AccompanyingDoc? = null

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "solution_id")
    val solutionId : Long = 0
}