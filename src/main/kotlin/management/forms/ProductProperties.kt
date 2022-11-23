package management.forms

import com.fasterxml.jackson.annotation.JsonProperty
import management.utils.ConstVariables.SCHEMA
import javax.persistence.*

@Entity
@Table(name = "product_properties", schema = SCHEMA)
data class ProductProperties(

    @Column(name = "unique_docs")
    val uniqueDocs: Boolean = false,

    @Column(name = "invoice_license")
    val invoiceLicense: Boolean = false, // for ikassa_license_12_season

    @Column(name = "skko_invoice")
    val skkoInvoice: Boolean = false, // for skko_register

    @Column(name = "billing_mode_use")
    val billingModeUse: Boolean = false, // for ikassa_register

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "properties_id")
    val propertiesId: Long = 0
}
