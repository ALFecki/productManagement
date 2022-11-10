package management.forms

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.Currency

data class ProductDto(
    @JsonProperty("alias")
    val alias : String,

    @JsonProperty("name")
    val name : String,

    @JsonProperty("comment")
    val comment : String? = null,

    @JsonProperty("price")
    val price : BigDecimal,

    @JsonProperty("tax")
    val tax : BigDecimal? = BigDecimal.ZERO,

    @JsonProperty("currency")
    val currency: String? = "",

    @JsonProperty("units")
    val units : Short?,

    @JsonProperty("round_total")
    val roundTotal : Boolean,

    @JsonProperty("accompanying_docs")
    val accompanyingDocs : List<AccDocumentDto>,

    @JsonProperty("dual_docs")
    val dualDocs : Boolean
)
