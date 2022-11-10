package management.forms

import com.fasterxml.jackson.annotation.JsonProperty

data class AccompanyingDocDto(
    @JsonProperty("path")
    val path : String,

    @JsonProperty("name")
    val name : String,

    @JsonProperty("field")
    val field : Map<String, String>,

    @JsonProperty("raw")
    val raw : Boolean
)
