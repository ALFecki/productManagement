package management.links.entities

import javax.persistence.*

@Entity
@Table(name = "products_accompanying_docs")
data class ProductAccDoc(
        @Column(name = "product_id")
        val productId : Long,

        @Column(name = "accompanying_doc_id")
        val accDocId : Long

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pr_acc_link_id")
    val linkId : Long = 0
}