package management.data.docs

import management.utils.ConstVariables.SCHEMA
import javax.persistence.*

@Entity
@Table(name = "required_docs", schema = SCHEMA)
data class RequiredDocs(
    @Column(name = "skko_contract")
    val skkoContract: Boolean = true,

    @Column(name = "skko_contract_application")
    val skkoContractApplication: Boolean = true,

    @Column(name = "sko_act")
    val skoAct: Boolean = true,

    @Column(name ="skko_connection_application")
    val skkoConnectionApplication: Boolean = true,

    @Column(name = "connection_notification")
    val connectionNotification: Boolean = true,

    @Column(name = "declaration_lk_unsafe")
    val declarationLkUnsafe: Boolean = true,

    @Column(name = "billing_mode")
    var billingMode: String = "full"

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "required_docs_id")
    val requiredDocsId: Long = 0

    fun toMap() : Map<String, Boolean> {
        return mapOf(
            "skko_contract" to skkoContract,
            "skko_contract_application" to skkoContractApplication,
            "sko_act" to skoAct,
            "skko_connection_application" to skkoConnectionApplication,
            "connection_notification" to connectionNotification,
            "declaration_lk_unsafe" to declarationLkUnsafe
        )
    }
}
