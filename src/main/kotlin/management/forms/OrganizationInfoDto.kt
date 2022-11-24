package management.forms

import com.fasterxml.jackson.annotation.JsonProperty

data class OrganizationInfoDto(
    @JsonProperty("unpN")
    val unpN: String,

    @JsonProperty("orgF")
    val orgF: String,

    @JsonProperty("docType")
    val docType: Int,

    @JsonProperty("organization")
    val organization: String,

    @JsonProperty("docValue")
    val docValue: HashMap<String, String>?,

    @JsonProperty("postAddress")
    val postAddress: HashMap<String, String>,

    @JsonProperty("urAddress")
    val urAddress: HashMap<String, String>,

    @JsonProperty("mail")
    val mail: String,

    @JsonProperty("phone")
    val phone: String,

    @JsonProperty("positionClient")
    val positionClient: String,

    @JsonProperty("fioClient")
    val fioClient: String,

    @JsonProperty("fioClient2")
    val fioClient2: String,

    @JsonProperty("positionClient2")
    val positionClient2: String,

    @JsonProperty("skkoNumber")
    val skkoNumber: String
)

