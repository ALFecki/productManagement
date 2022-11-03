package management.forms

import com.fasterxml.jackson.annotation.JsonProperty

data class DocumentDto(
    @JsonProperty("contractData")
    val contractData: DocumentInfoDto,

    @JsonProperty("equipment")
    val equipment: HashMap<String, String>
)
