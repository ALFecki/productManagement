package management.forms

import com.fasterxml.jackson.annotation.JsonProperty

data class DocumentDto(
    @JsonProperty("alias")
    val alias: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("path")
    val path: String
)
