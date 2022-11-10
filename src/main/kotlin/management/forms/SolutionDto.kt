package management.forms

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class SolutionDto (

    @JsonProperty("alias")
    val alias : String,

    @JsonProperty("name")
    val name : String,

    @JsonProperty("contents")
    val contents : List<ProductDto>,

    @JsonProperty("related")
    val related : List<ProductDto>,

    @JsonProperty("price")
    val price : BigDecimal,

    @JsonProperty("accompanying_docs")
    val accompanyingDoc: List<AccDocumentDto>,

    @JsonProperty("equipment")
    val equipment : List<ProductDto>,

    @JsonProperty("extra_vars")
    val extraVars : Map<String, String>,

    @JsonProperty("legal_name")
    val legalName : String,

    @JsonProperty("version")
    val version : String,

    @JsonProperty("instruction")
    val instruction : AccDocumentDto

    ) {
}