package management.data.utils

import javax.persistence.*


@Entity
@Table(name = "email_mode", schema = "ikassa")
data class PartnerFormEmailMode(

    @Column(name = "name")
    val name: String,

    @Column(name = "send_to_client")
    val sendToClient: Boolean,

    @Column(name = "send_to_partner")
    val sendToPartner: Boolean
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_mode_id")
    val emailModeId : Long = 0
}
