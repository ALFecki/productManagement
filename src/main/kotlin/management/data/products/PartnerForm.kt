package management.data.products

import com.vladmihalcea.hibernate.type.array.IntArrayType
import com.vladmihalcea.hibernate.type.array.StringArrayType
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import management.utils.ConstVariables.SCHEMA
import org.hibernate.annotations.*
import javax.persistence.*
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Table


@TypeDefs(
    TypeDef(name = "string-array", typeClass = StringArrayType::class),
    TypeDef(name = "int-array", typeClass = IntArrayType::class),
    TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
)
@Entity
@Table(name = "partner_form", schema = SCHEMA, )
data class PartnerForm(

    @Column(name = "unp")
    val UNP : Int,

    @Column(name = "name")
    val name : String,

    @Column(name = "logo")
    val logo: String,

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(
        targetEntity = Solution::class,
        cascade = [CascadeType.MERGE],
    )
    @JoinTable(
        name = "form_solution",
        schema = SCHEMA,
        joinColumns = [JoinColumn(name = "partner_form_id")],
        inverseJoinColumns = [JoinColumn(name = "solution_id")]
    )
    val solutions: List<Solution> = listOf(),

    @Column(name = "name_remap")
    @Type(type = "jsonb")
    val nameRemap: Map<String, String> = hashMapOf(),


    @Column(name = "email_mode")
    val emailModeName: String = PartnerFormEmailMode.BOTH.name,


    @Column(name = "emails")
    @Type(type = "string-array")
    val partnerEmail: Array<String> = arrayOf(),

    @Column(name = "allow_manual")
    val allowManual: Boolean = true,

    @Column(name = "description")
    val formDescription: String = "Эта форма позволяет заполнить данные и скачать готовый пакет документов",


    @Column(name = "available_periods")
    @Type(type = "int-array")
    val availablePeriods: Array<Int> = arrayOf(6, 12),

    @Column(name = "slug")
    val slug: String ?= null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_form_id")
    val partnerFormId : Long = 0

    @Transient
    val emailMode : PartnerFormEmailMode = PartnerFormEmailMode.valueOf(emailModeName)
}

enum class PartnerFormEmailMode(
    val sendToClient: Boolean=true,
    val sendToPartner: Boolean=false
) {
    NONE(sendToClient = false, sendToPartner = false),
    BOTH(sendToClient = true, sendToPartner = true),
    PARTNER_ONLY(sendToClient = false, sendToPartner = true),
    CLIENT_ONLY(sendToClient = true, sendToPartner = false)
}
