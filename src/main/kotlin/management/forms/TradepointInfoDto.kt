package management.forms

import com.fasterxml.jackson.annotation.JsonProperty

data class TradepointInfoDto(
    @JsonProperty("torgName")
    val torgName: String,

    @JsonProperty("torgAddress")
    val torgAddress: String,

    @JsonProperty("torgType")
    val torgType: String,

    @JsonProperty("unitsCashbox")
    val unitsCashbox: Int,

    @JsonProperty("strTimeWork")
    val strTimeWork: List<String>,

    @JsonProperty("glnNumber")
    val glnNumber: String? = null,

    @JsonProperty("glnToggle")
    val glnToggle: String? = null
)
