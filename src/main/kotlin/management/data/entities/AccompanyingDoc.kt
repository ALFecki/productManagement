package management.data.entities


import javax.persistence.*
import management.utils.ConstVariables.SCHEMA


@Entity
@Table(name = "accompanying_doc", schema = SCHEMA)
data class AccompanyingDoc (

        @Column(name = "path")
        val path: String,

        @Column(name = "name")
        val name: String,
//
//        @TypeDef(type = DataType.JSON)
//        @Column(name = "field")
//        val fields: Map<String, String>? = null,

        @Column(name = "raw")
        val raw: Boolean = false
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accompanying_doc_id")
    var accompanyingDocId: Long = 0
}