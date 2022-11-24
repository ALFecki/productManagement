package management.forms

import com.fasterxml.jackson.annotation.JsonProperty

data class DocumentInfoDto(
    @JsonProperty("organizationInfo")
    val organizationInfo: OrganizationInfoDto,

    @JsonProperty("bankInfo")
    var bankInfo: HashMap<String, String>,

    @JsonProperty("tradeInfo")
    val tradeInfo: List<TradePointInfoDto>
)
