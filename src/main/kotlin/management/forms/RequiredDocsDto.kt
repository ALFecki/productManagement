package management.forms

import com.fasterxml.jackson.annotation.JsonProperty


data class RequiredDocsDto(
    @JsonProperty("skko_contract")
    val skkoContract: Boolean = true,

    @JsonProperty("skko_contract_application")
    val skkoContractApplication: Boolean = true,

    @JsonProperty("sko_act")
    val skoAct: Boolean = true,

    @JsonProperty("skko_connection_application")
    val skkoConnectionApplication: Boolean = true,

    @JsonProperty("connection_notification")
    val connectionNotification: Boolean = true,

    @JsonProperty("declaration_lk_unsafe")
    val declarationLkUnsafe: Boolean = true,

    @JsonProperty("contract")
    val contract: Boolean = true,

    @JsonProperty("billing_mode")
    val billingMode: String = "full"

) {
    fun toMap() : Map<String, Boolean> {
        return mapOf(
            "skko_contract" to skkoContract,
            "skko_contract_application" to skkoContractApplication,
            "sko_act" to skoAct,
            "skko_connection_application" to skkoConnectionApplication,
            "connection_notification" to connectionNotification,
            "declaration_lk_unsafe" to declarationLkUnsafe,
            "contract" to contract,
//            "invoice_license" to invoiceLicense,
//            "skko_invoice" to skkoInvoice
        )
    }
}
