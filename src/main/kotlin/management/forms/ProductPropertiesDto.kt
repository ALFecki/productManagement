package management.forms

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductPropertiesDto(

    @JsonProperty("unique_docs")
    val uniqueDocs: Boolean = false,

    @JsonProperty("invoice_license")
    val invoiceLicense: Boolean = false, // for ikassa_license_12_season

    @JsonProperty("skko_invoice")
    val skkoInvoice: Boolean = false, // for skko_register

    @JsonProperty("billing_mode_use")
    val billingModeUse: Boolean = false // for ikassa_register

)
