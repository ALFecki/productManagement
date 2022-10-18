package management.data.entities

import management.utils.ConstVariables.SCHEMA
import javax.persistence.*


@Entity
@Table(name = "partner_form", schema = SCHEMA)
data class PartnerForm(

    @Column(name = "UNP")
    val UNP : Int,

    @Column(name = "name")
    val name : String,

    @Column(name = "logo")
    val logo: String,

    @ManyToMany(
        targetEntity = Solution::class,
        cascade = [CascadeType.ALL],

    )
    @JoinTable(
        name = "form_solution",
        schema = SCHEMA,
        joinColumns = [JoinColumn(name = "partner_form_id")],
        inverseJoinColumns = [JoinColumn(name = "solution_id")]
    )
    val solutions: List<Solution> = listOf(),

//TODO
//    val nameRemap: Map<String, String> = hashMapOf(),
//    @Column(name = "email_mode")
//    val emailMode: PartnerFormEmailMode = PartnerFormEmailMode.BOTH,

    @Column(name = "emails")
    val partnerEmail: List<String> = emptyList(),

    @Column(name = "allow_manual")
    val allowManual: Boolean = true,

    @Column(name = "description")
    val formDescription: String = "Эта форма позволяет заполнить данные и скачать готовый пакет документов",

    @Column(name = "available_periods")
    val availablePeriods: List<Short> = listOf(6, 12),

    @Column(name = "slug")
    val slug: String ?= null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_form_id")
    val partnerFormId : Long = 0
}

enum class PartnerFormEmailMode(
    val alias : String,
    val sendToClient: Boolean=true,
    val sendToPartner: Boolean=false
) {
    NONE(alias = "NONE",sendToClient = false, sendToPartner = false),
    BOTH(alias = "BOTH",sendToClient = true, sendToPartner = true),
    PARTNER_ONLY(alias = "PARTNER_ONLY",sendToClient = false, sendToPartner = true),
    CLIENT_ONLY(alias = "CLIENT_ONLY",sendToClient = true, sendToPartner = false)
}
