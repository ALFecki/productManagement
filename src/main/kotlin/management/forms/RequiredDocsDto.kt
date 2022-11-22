package management.forms

import com.fasterxml.jackson.annotation.JsonProperty


data class RequiredDocsDto(
    @JsonProperty("skko_contract")
    val skkoContract: Boolean = false,

    @JsonProperty("skko_contract_application")
    val skkoContractApp: Boolean = false,

    @JsonProperty("sko_act")
    val skoAct: Boolean = false,

    @JsonProperty("skko_connection_application")
    val skkoConnectionApp: Boolean = false,

    @JsonProperty("connection_notification")
    val notification: Boolean = false,

    @JsonProperty("declaration_lk_unsafe")
    val declarationLkUnsafe: Boolean = false,

    @JsonProperty("contract")
    val contract: Boolean = false,

    @JsonProperty("invoice_license")
    val invoiceLicense: Boolean = false,

    @JsonProperty("skko_invoice")
    val skkoInvoice: Boolean = false

)
