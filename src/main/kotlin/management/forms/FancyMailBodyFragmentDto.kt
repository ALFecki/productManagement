package management.forms

import com.fasterxml.jackson.annotation.JsonProperty

data class FancyMailBodyFragmentDto(
    @JsonProperty("vars")
    var vars: HashMap<String, Any>? = hashMapOf(),

    @JsonProperty("template")
    var template: String? = null,

    @JsonProperty("template_content")
    var templateContent: FancyMailBodyFragmentDto? = null,

    @JsonProperty("template_contents")
    var templateContents: List<FancyMailBodyFragmentDto>? = null,

    @JsonProperty("template_file")
    var templateFile: String? = null
)
