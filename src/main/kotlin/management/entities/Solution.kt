package management.entities

import jakarta.persistence.*
import java.math.BigDecimal
import javax.management.monitor.StringMonitor


@Entity
@Table(name = "Solution", schema = "ikassa")
data class Solution (

    @GeneratedValue
    @Id val solutionId : Int? = null,

    @Column(name = "alias")
    val alias : String,

    @Column(name = "name")
    val name : String,

//    @Column(name = "contents")
//    val contents

//    @Column(name = "related")
//    val related : List<Product> = listOf()

    @Column(name = "price")
    val price : BigDecimal? = null,

    @Column(name = "legal_name")
    val legalName : String,

    @Column(name = "version")
    val version : String = "2.4.0"


//    Column(name = )

)