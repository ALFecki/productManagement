package management.data.docs

import javax.persistence.*


@Entity
@Table(name = "document", schema = "ikassa")
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
    val documentId : Long = 0
}
