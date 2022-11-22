package management.forms

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductPropertiesDto(

    @JsonProperty("unique_docs")
    val uniqueDocs: Boolean = false,

    @JsonProperty("billing_mode")
    val billingMode: String = "none"
)
