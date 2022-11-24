package management.forms

import com.fasterxml.jackson.annotation.JsonProperty
import management.data.docs.RequiredDocs
import java.math.BigDecimal

data class SolutionDto(

    @JsonProperty("alias")
    val alias: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("contents")
    val contents: List<ProductDto> = listOf(),

    @JsonProperty("related")
    val related: List<ProductDto> = listOf(),

    @JsonProperty("price")
    val price: BigDecimal? = null,

    @JsonProperty("accompanying_docs")
    val accompanyingDoc: List<AccompanyingDocDto> = listOf(),

    @JsonProperty("equipment")
    val equipment: List<ProductDto> = listOf(),

    @JsonProperty("extra_vars")
    val extraVars: Map<String, String> = mapOf(),

    @JsonProperty("legal_name")
    val legalName: String,

    @JsonProperty("version")
    val version: String = "2.4.0",

    @JsonProperty("instruction")
    val instruction: AccompanyingDocDto? = null,

    @JsonProperty("required_docs")
    val requiredDocs: RequiredDocs = RequiredDocs()

)