package management.services

import jakarta.inject.Singleton
import management.data.docs.Document
import management.data.docs.RenderedDocument
import management.data.products.Product
import management.data.products.ProductTotal
import management.data.products.Solution
import management.data.repositories.DocumentRepository
import management.forms.DocumentDto
import management.forms.OrganizationInfoDto
import management.utils.FilePath.PATH_TO_FM
import management.utils.FilePath.PATH_TO_PAYMENT_LICENSE
import management.utils.FilePath.PATH_TO_PAYMENT_REGISTRATION
import management.utils.FilePath.PATH_TO_PAYMENT_SERVICE
import management.utils.FilePath.PATH_TO_PAYMENT_SKKO
import management.utils.FilePath.PATH_TO_SMART
import management.utils.asWords
import management.utils.replaceMultiple
import management.utils.toByteArray
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.zip.ZipOutputStream


@Singleton
class FillDocumentService (private val documentRepository: DocumentRepository) {

    private val EMPTY_FIELD = "______________________________________"

    val payments = mapOf(
        PATH_TO_SMART to listOf("100", "0.2"),
        PATH_TO_PAYMENT_REGISTRATION to listOf("35", "0"),
        PATH_TO_PAYMENT_LICENSE to listOf("15", "0"),
        PATH_TO_PAYMENT_SKKO to listOf("2.5", "0.2"),
        PATH_TO_PAYMENT_SERVICE to listOf("25", "0.2"),
        PATH_TO_FM to listOf("290", "0.2")
    )

    fun fillProductDocument(path : String, product: Product, productTotal: ProductTotal, full : DocumentDto? = null, solution: Solution? = null) : ByteArray {
        val defaultInfo = mutableMapOf<String, String?>(
            "{caption}" to product.comment,
            "{price}" to productTotal.priceFormatted,
            "{tax}" to productTotal.taxFormatted,
            "{tax_sum}" to productTotal.taxSumFormatted,
            "{tax_sum_text}" to productTotal.taxSum.asWords(),
            "{tax_total_text}" to productTotal.taxTotal.asWords(),
            "{tax_total}" to productTotal.taxTotalFormatted,
            "{cost}" to productTotal.costFormatted,
            "{total}" to productTotal.totalFormatted,
            "{subtotal}" to productTotal.subTotalFormatted,
            "{total_text}" to productTotal.totalText,
            "{total_value}" to productTotal.total.toString(),
            "{quantity}" to productTotal.quantityFormatted,

            "TAXTOTALTEXT" to productTotal.taxTotal.asWords(),
            "TAXTOTALVALUE" to productTotal.taxTotal.toString(),
            "TAXSUMTEXT" to productTotal.taxSum.asWords(),
            "TAX_TOTAL" to productTotal.taxTotalFormatted,
            "TOTAL_TEXT" to productTotal.totalText,
            "TOTAL_VALUE" to productTotal.total.toString(),
            "TOTAL" to productTotal.totalFormatted
        )

        solution?.extraVars?.let {
            defaultInfo.putAll(it)
        }

        val pew = full?.equipment?.getOrDefault("bank", null) ?: solution?.extraVars?.getOrDefault("PROCESSINGPROVIDER", null)
        pew?.let {
            if(it.trim() != "") {
                defaultInfo["PROCESSINGPROVIDER"] = it.trim()
            }
        }

        val realPath =if(full != null) {
            val organization = full.contractData.organizationInfo
            mapOf(
                "CONTRACT_HEADER" to hardwareContractHeader(organization),
                "{company[unp]}" to organization.unpN,
                "UNP" to organization.unpN,
                "{company[organization]}" to getOrganizationName(organization),
                "ORG_NAME" to getOrganizationName(organization),
                "{company[post_address]}" to "${organization.postAddress["index"]} ${organization.postAddress["address"]}",
                "{company[legal_address]}" to "${organization.urAddress["index"]} ${organization.urAddress["address"]}",
                "{company[email]}" to organization.mail,
                "{company[phone]}" to organization.phone,
                "BANK_ACCOUNT" to full.contractData.bankInfo["payment"],
                "BANK_BIC" to full.contractData.bankInfo["BICBank"],
                "BANK_NAME" to full.contractData.bankInfo["nameBank"],
                "BANK_ADDRESS" to full.contractData.bankInfo.getOrDefault("bankAddress", EMPTY_FIELD),
            ).let {
                defaultInfo.putAll(it)
            }
            if(product.dualDocs) {
                path.replace("docs/products", "docs/fill_auto/products")
            } else {
                path
            }
        } else {
            if(product.dualDocs) {
                path.replace("docs/products", "docs/fill_manual/products")
            } else {
                path
            }
        }
        return renderDocument(realPath) {
            it.replaceMultiple(defaultInfo)
        }

    }

    fun fillProductsDocuments(products : List<Product>, eqTotal : Short, fullData : DocumentDto? = null, solution: Solution? = null) : List<RenderedDocument> {
        val documents : MutableList<RenderedDocument> = mutableListOf()
        products.forEach { product ->
            val totalProduct = product.toTotal(eqTotal)
            product.accompanyingDocs.forEach { accompanyingDoc ->
                val fileName = accompanyingDoc.name.ifEmpty { accompanyingDoc.path }
                val content = if(accompanyingDoc.raw) {
                    this.getFileAsByteArray(accompanyingDoc.path)!!
                } else {
                    fillProductDocument(accompanyingDoc.path, product, totalProduct, fullData, solution)
                }
                documents.add(RenderedDocument(fileName, content, accompanyingDoc.path.substringAfterLast('.', "docx")))
            }
        }
        return documents
    }


    fun getFile(filename: String): InputStream? {
        return this::class.java.classLoader.getResourceAsStream(filename)
    }

    fun getFileAsByteArray(filename : String) : ByteArray? {
        val stream = this::class.java.classLoader.getResourceAsStream(filename)
        stream ?: return null
        val byteArray = stream.readBytes()
        stream.close()
        return byteArray
    }

    private fun stripIp(organization: OrganizationInfoDto): String {
        return when {
            organization.organization.startsWith("ип ", true) -> {
                organization.organization.replace("ип ", "", true)
            }
            organization.organization.startsWith("индивидуальный предприниматель ", true) -> {
                organization.organization.replace("индивидуальный предприниматель ", "", true)
            }
            else -> {
                organization.organization
            }
        }
    }
    private fun getOrganizationName(organization: OrganizationInfoDto): String {
        return if (organization.orgF == "IP") {
            "Индивидуальный предприниматель ${stripIp(organization)}"
        } else {
            organization.organization //org.name
        }
    }


    fun renderDocument(path: String, pew: (document: XWPFDocument) -> Unit): ByteArray {
        println(path)
        getFile(path).use { docInputStream ->
            XWPFDocument(docInputStream).use { document ->
                pew(document)
                return document.toByteArray()
            }
        }
    }

    fun createZipArchive(renderedDocuments: List<RenderedDocument>): ByteArray {
        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { zipOut ->
            renderedDocuments.forEach { rd ->
                rd.toZip(zipOut)
            }
        }
        return out.toByteArray()
    }

    fun powerPaperText2(organization : OrganizationInfoDto): String {
        val DOC_NUMBER_FIELD = organization.docValue?.get("DOC_NUMBER_FIELD")
        val DATE_FIELD = organization.docValue?.get("DATE_FIELD")
        val numField = if(DOC_NUMBER_FIELD != null && DOC_NUMBER_FIELD != "") {
            "№$DOC_NUMBER_FIELD"
        } else {
            ""
        }
        val dateField = if(DATE_FIELD != null && DATE_FIELD != "") {
            "от $DATE_FIELD"
        } else {
            ""
        }
        return "${DocWithEnding.getById(organization.docType).value} $numField $dateField"
    }

    fun contractHeader(org: OrganizationInfoDto): String {
        val oe = if (org.orgF == "IP") {
            "ый"
        } else {
            "ое"
        }
        return "${getOrganizationName(org)}, именуем${oe} в дальнейшем «Пользователь»," +
                " в лице ${org.positionClient2.toLowerCase()} ${org.fioClient2}, действующего на основании ${powerPaperText2(org)}"
    }

    fun hardwareContractHeader(organiztion: OrganizationInfoDto): String {
        return contractHeader(organiztion).replace("Пользователь", "Покупатель")
    }












    public fun exportDefaultDocs() : List<Document> {
        return documentRepository.saveAll(
            listOf(
                Document(
                    name = "01-1 Заявление о присоединении к публичному договору СККО",
                    path = "skko/contract.docx",
                    alias = "skko_contract"
                ),
                Document(
                    name = "01-2 (если договор на СКНО заключен) Дополнительное соглашение",
                    path = "skko/contract_application.docx",
                    alias = "skko_contract_application"
                ),
                Document(
                    name = "02 Заявка на регистрацию и подключение ПК IKASSA",
                    path = "skko/connection_application.docx",
                    alias = "skko_connection_application"
                ),
                Document(
                    name = "03 Реквизиты для оплаты Регистрации и информационного обслуживания ПК IKASSA в СККО",
                    path = "skko/invoice.docx",
                    alias = "skko_invoice"
                ),
                Document(
                    name = "04 Реквизиты для оплаты Регистрации и абонентского обслуживания ПК IKASSA",
                    path = "invoice.docx",
                    alias = "invoice"
                ),
                Document(
                    name = "04 Реквизиты для оплаты Регистрации ПК IKASSA",
                    path = "invoice_register.docx",
                    alias = "invoice_register"
                ),
                Document(
                    name = "04 Реквизиты для оплаты абонентского обслуживания ПК IKASSA",
                    path = "invoice_license.docx",
                    alias = "invoice_license"
                ),
                Document(
                    name = "06 Рекомендованная форма доверенности (при оформлении уполномоченным представителем)",
                    path = "attorney.docx",
                    alias = "attorney"
                ),
                Document(
                    name = "07 Уведомление о присоединении к договору",
                    path = "connection_notification.docx",
                    alias = "connection_notification"
                ),
                Document(
                    name = "08 Акт приема-передачи СКО",
                    path = "skko/sko_act.docx",
                    alias = "sko_act" // skko_sko_act
                ),
                Document(
                    name = "09 Заявление о доступе в Личный Кабинет по паролю",
                    path = "declaration_lk_unsafe.docx",
                    alias = "declaration_lk_unsafe"
                )
            )
        )
    }

    enum class DocWithEnding(val id: Int, val value: String) {
        EMPTY(0, ""),
        CHARTER(1, "Устава"),
        CERTIFICATE(2, "Свидетельства о гос. регистрации"),
        DOC(3, "Устава и договора"),
        ATTORNEY(4, "Доверенности"),
        CONTRACT(5, "Договора"),
        ORDER(6, "Приказа");

        companion object {
            fun getById(id: Int): DocWithEnding {
                return values()
                    .find { it.id == id } ?: throw IllegalStateException("Unknown status with id $id")
            }

            fun from(id: Int): DocWithEnding {
                return values()
                    .find { it.id == id } ?: throw IllegalStateException("Unknown doc with id $id")
            }
        }
    }

}

