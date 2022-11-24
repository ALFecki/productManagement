package management.data.products

import com.vladmihalcea.hibernate.type.array.IntArrayType
import com.vladmihalcea.hibernate.type.array.StringArrayType
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import management.data.utils.PartnerFormEmailMode
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
@Table(name = "partner_form", schema = SCHEMA)
data class PartnerForm(

    @Column(name = "unp")
    val unp: Int,

    @Column(name = "name")
    val name: String,

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
    val slug: String? = null,

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToOne(
        targetEntity = PartnerFormEmailMode::class,
        cascade = [CascadeType.ALL]
    )
    @JoinTable(
        name = "partner_form_email_mode",
        schema = SCHEMA,
        joinColumns = [JoinColumn(name = "partner_form_id")],
        inverseJoinColumns = [JoinColumn(name = "email_mode_id")]
    )
    val emailMode: PartnerFormEmailMode
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_form_id")
    val partnerFormId: Long = 0

}

