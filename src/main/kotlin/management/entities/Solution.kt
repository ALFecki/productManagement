package management.entities

import java.math.BigDecimal
import javax.persistence.*
import management.utils.ConstVariables.SCHEMA
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption


@Entity
@Table(name = "solution", schema = SCHEMA)
data class Solution(
        @Column(name = "alias")
        val alias : String,

        @Column (name = "name")
        val name : String,

        @LazyCollection(LazyCollectionOption.FALSE)
        @ManyToMany(
                targetEntity = Product::class,
                cascade = [CascadeType.MERGE]
        )
        @JoinTable(
                name = "solutions_products",
                schema = SCHEMA,
                joinColumns = [JoinColumn(name = "solution_id")],
                inverseJoinColumns = [JoinColumn(name = "product_id")]
        )
        var contents: Set<Product> = setOf(),

        @LazyCollection(LazyCollectionOption.FALSE)
        @ManyToMany(
                targetEntity = Product::class,
                cascade = [CascadeType.MERGE]
        )
        @JoinTable(
                name = "solutions_related_products",
                schema = SCHEMA,
                joinColumns = [JoinColumn(name = "solution_id")],
                inverseJoinColumns = [JoinColumn(name = "product_id")]
        )
        var related: Set<Product> = setOf(),

        @Column(name = "price")
        val price : BigDecimal? = null, // WHY?

        @LazyCollection(LazyCollectionOption.FALSE)
        @ManyToMany(
                targetEntity = AccompanyingDoc::class,
                cascade = [(CascadeType.MERGE)]
        )
        @JoinTable(
                name = "solutions_accompanying_docs",
                schema = SCHEMA,
                joinColumns = [JoinColumn(name = "solution_id")],
                inverseJoinColumns = [JoinColumn(name = "accompanying_doc_id")]
        )
        var accompanyingDoc: Set<AccompanyingDoc> = setOf(),

        @LazyCollection(LazyCollectionOption.FALSE)
        @ManyToMany(
                targetEntity = Product::class,
                cascade = [(CascadeType.MERGE)]
        )
        @JoinTable(
                name = "solutions_equipment",
                schema = SCHEMA,
                joinColumns = [JoinColumn(name = "solution_id")],
                inverseJoinColumns = [JoinColumn(name = "product_id")]
        )
        var equipment: Set<Product> = setOf(),

//    @TypeDef(type = DataType.JSON)
//    @Column(name = "extra_vars")
//    val extraVars : Map<String, String>,

        @Column(name ="legal_name")
        val legalName : String,

        @Column(name = "version")
        val version : String = "2.4.0",


        @LazyCollection(LazyCollectionOption.FALSE)
        @OneToOne(
            targetEntity = AccompanyingDoc::class,
            cascade = [(CascadeType.ALL)],
        )
        @JoinTable(
            name = "solution_instruction",
            schema = SCHEMA,
            joinColumns = [JoinColumn(name = "solution_id")],
            inverseJoinColumns = [JoinColumn(name = "accompanying_doc_id")] )
        val forcedInstructionPdf : AccompanyingDoc? = null

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "solution_id")
    val solutionId : Long = 0
}