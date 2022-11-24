package management.forms

import com.fasterxml.jackson.annotation.JsonProperty
import management.data.products.ProductProperties
import java.math.BigDecimal

data class ProductDto(
    @JsonProperty("alias")
    val alias: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("comment")
    val comment: String = "",

    @JsonProperty("price")
    val price: BigDecimal,

    @JsonProperty("tax")
    val tax: BigDecimal = BigDecimal.ZERO,

    @JsonProperty("currency")
    val currency: String = "",

    @JsonProperty("units")
    val units: String = "шт.",

    @JsonProperty("round_total")
    val roundTotal: Boolean = false,

    @JsonProperty("accompanying_docs")
    val accompanyingDocs: List<AccompanyingDocDto> = listOf(),

    @JsonProperty("dual_docs")
    val dualDocs: Boolean = false,

    @JsonProperty("properties")
    val productProperties: ProductProperties = ProductProperties()
)
