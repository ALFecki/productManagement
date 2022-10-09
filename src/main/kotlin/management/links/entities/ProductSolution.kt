package management.links.entities

import javax.persistence.*

@Entity
@Table(name = "solutions_products")
data class ProductSolution(
    @Column(name = "solution_id")
    val solutionId : Long,

    @Column(name = "product_id")
    val productId : Long

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sol_prod_link_id")
    val linkId : Long = 0
}

