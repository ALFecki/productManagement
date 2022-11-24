package management.data.utils

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal


data class UpdateProduct(
    @JsonProperty("name")
    val name: String?,

    @JsonProperty("comment")
    val comment: String?,

    @JsonProperty("price")
    val price: BigDecimal?,

    @JsonProperty("tax")
    val tax: BigDecimal?,

    @JsonProperty("currency")
    val currency: String?,

    @JsonProperty("units")
    val units: String?,

    @JsonProperty("round_total")
    val roundTotal: Boolean?,

    @JsonProperty("dual_docs")
    val dualDocs: Boolean?
)
