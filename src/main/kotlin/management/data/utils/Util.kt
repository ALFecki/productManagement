package management.data.utils

import management.utils.ConstVariables.SCHEMA
import javax.persistence.*


@Table(name = "util", schema = SCHEMA)
data class Util(

    @Column(name = "name")
    val name : String,

    @Column(name = "data")
    val data : String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val utilId : Long = 0
}

