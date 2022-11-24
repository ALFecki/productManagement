package management.data.products


import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import management.utils.ConstVariables.SCHEMA
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.*

@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
@Entity
@Table(name = "accompanying_doc", schema = SCHEMA)
data class AccompanyingDoc(

    @Column(name = "path")
    val path: String,

    @Column(name = "name")
    val name: String,

    @Column(name = "field")
    @Type(type = "jsonb")
    val fields: Map<String, String>? = mapOf(),

    @Column(name = "raw")
    val raw: Boolean = false
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accompanying_doc_id")
    var accompanyingDocId: Long = 0
}