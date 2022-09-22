package management.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "AccompanyingDoc", schema = "ikassa")
data class AccompanyingDoc (

    @GeneratedValue
    @Id val accDocId: Int,

    @Column(name = "path")
    val path: String,

    @Column(name = "name")
    val name: String = "",

//    @Column(name = "field")
//    val fields: Map<String, String>? = mapOf(),

    /** Process document as raw file or as templated docx file */
    @Column(name = "raw")
    val raw: Boolean = false
)