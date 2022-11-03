package management.data.docs

import management.utils.ConstVariables.SCHEMA
import javax.persistence.*


@Entity
@Table(name = "document", schema = SCHEMA)
data class Document(
    @Column(name = "alias")
    val alias : String,

    @Column(name = "name")
    val name : String,

    @Column (name = "path")
    val path : String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    var documentId : Long = 0
}
