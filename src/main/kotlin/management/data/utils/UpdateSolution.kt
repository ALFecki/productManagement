package management.data.utils

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class UpdateSolution(
    @JsonProperty("name")
    val name: String?,

    @JsonProperty("price")
    val price: BigDecimal?,

    @JsonProperty("extra_vars")
    val extraVars : Map<String, String>?,

    @JsonProperty("legal_name")
    val legalName: String?,

    @JsonProperty("version")
    val version: String?,

)
