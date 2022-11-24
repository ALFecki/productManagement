package management.forms

import com.fasterxml.jackson.annotation.JsonProperty
import management.data.utils.PartnerFormEmailMode

data class PartnerFormDto(

    @JsonProperty("unp")
    val unp: Int,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("logo")
    val logo: String,

    @JsonProperty("solutions")
    val solutions: List<SolutionDto> = listOf(),

    @JsonProperty("name_remap")
    val nameRemap: Map<String, String> = hashMapOf(),

    @JsonProperty("emails")
    val partnerEmail: List<String> = listOf(),

    @JsonProperty("allow_manual")
    val allowManual: Boolean = true,

    @JsonProperty("description")
    val formDescription: String = "Эта форма позволяет заполнить данные и скачать готовый пакет документов",

    @JsonProperty("available_periods")
    val availablePeriods: List<Int> = listOf(6,12),

    @JsonProperty("slug")
    val slug: String = "",

    @JsonProperty("email_mode")
    val emailMode: PartnerEmailModeDto

)

data class PartnerEmailModeDto(
    @JsonProperty("name")
    val name: String,

    @JsonProperty("send_to_client")
    val sendToClient: Boolean,

    @JsonProperty("send_to_partner")
    val sendToPartner: Boolean
) {
    companion object {
        fun makeEmailMode(data: PartnerEmailModeDto) : PartnerFormEmailMode {
            return PartnerFormEmailMode(
                name = data.name,
                sendToClient = data.sendToClient,
                sendToPartner = data.sendToPartner
            )
        }
    }

}
