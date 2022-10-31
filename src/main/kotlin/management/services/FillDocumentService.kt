package management.services

import com.microsoft.schemas.office.office.STHow
import jakarta.inject.Singleton
import management.data.docs.Document
import management.data.docs.RenderedDocument
import management.data.products.AccompanyingDoc
import management.data.products.Product
import management.data.products.ProductTotal
import management.data.products.Solution
import management.data.repositories.DocumentRepository
import management.data.repositories.SolutionRepository
import management.forms.DocumentDto
import management.forms.OrganizationInfoDto
import management.utils.FilePath.PATH_TO_FM
import management.utils.FilePath.PATH_TO_PAYMENT_LICENSE
import management.utils.FilePath.PATH_TO_PAYMENT_REGISTRATION
import management.utils.FilePath.PATH_TO_PAYMENT_SERVICE
import management.utils.FilePath.PATH_TO_PAYMENT_SKKO
import management.utils.FilePath.PATH_TO_SMART
import management.utils.asWords
import management.utils.makeBorder
import management.utils.replaceMultiple
import management.utils.toByteArray
import org.apache.poi.xwpf.usermodel.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.zip.ZipOutputStream


@Singleton
class FillDocumentService (
    private val documentRepository: DocumentRepository,
    private val productService: ProductService,
    private val solutionService: SolutionService
    ) {

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

    fun fillIkassaInvoice(quantity : Short, period : Short, extraPrefix : String = "" ) : RenderedDocument {
        val oneMonthLicense = "ikassa_license"

        val productKey = (if (period == 1.toShort()) {
            oneMonthLicense
        } else {
            "ikassa_license_$period"
        }) + extraPrefix

        val license = productService.getProductByAlias(productKey) ?: productService.getProductByAlias(oneMonthLicense)
            ?: throw IllegalStateException("Cannot find license product")
        val licenseTotal = license.toTotal()

        val connection = productService.getProductByAlias("ikassa_register")
            ?: throw IllegalStateException("Cannot find registration product")
        val connectionTotal = connection.toTotal(quantity)

        val total = connectionTotal.total + licenseTotal.total
        val totalFormatted = "$total ${connection.currency}"

        val cost = connectionTotal.cost + licenseTotal.cost
        val costFormatted = "$cost ${connection.currency}"

        val subTotal = connectionTotal.subTotal + licenseTotal.subTotal
        val subTotalFormatted = "$subTotal ${connection.currency}"

        return renderDocumentFromMap("docs", documentRepository.findByAlias("invoice")!!, mapOf(
            "{connection[caption]}" to connection.comment,
            "{connection[price]}" to connectionTotal.priceFormatted,
            "{connection[tax]}" to connectionTotal.taxFormatted,
            "{connection[cost]}" to connectionTotal.costFormatted,
            "{connection[subtotal]}" to connectionTotal.subTotalFormatted,
            "{connection[total]}" to connectionTotal.totalFormatted,

            "{tariff[caption]}" to license.comment,
            "{tariff[price]}" to licenseTotal.priceFormatted,
            "{tariff[tax]}" to licenseTotal.taxFormatted,
            "{tariff[cost]}" to licenseTotal.costFormatted,
            "{tariff[subtotal]}" to licenseTotal.subTotalFormatted,
            "{tariff[total]}" to licenseTotal.totalFormatted,

            "{quantity}" to connectionTotal.quantityFormatted,
            "{cost}" to costFormatted,
            "{subtotal}" to subTotalFormatted,
            "{total}" to totalFormatted,
            "{total_text}" to total.asWords()
        ))
    }

    fun fillIkassaRegistration(quantity: Short = 1): RenderedDocument {
        val connection = productService.getProductByAlias("ikassa_register_only")
            ?: throw IllegalStateException("Cannot find registration product")
        val connectionTotal = connection.toTotal(quantity)

        val total = connectionTotal.total
        val totalFormatted = "$total ${connection.currency}"

        val cost = connectionTotal.cost
        val costFormatted = "$cost ${connection.currency}"

        val subTotal = connectionTotal.subTotal
        val subTotalFormatted = "$subTotal ${connection.currency}"

        return renderDocumentFromMap("docs", documentRepository.findByAlias("invoice_register")!!, mapOf(
            "{connection[caption]}" to connection.comment,
            "{connection[price]}" to connectionTotal.priceFormatted,
            "{connection[tax]}" to connectionTotal.taxFormatted,
            "{connection[cost]}" to connectionTotal.costFormatted,
            "{connection[subtotal]}" to connectionTotal.subTotalFormatted,
            "{connection[total]}" to connectionTotal.totalFormatted,

            "{quantity}" to connectionTotal.quantityFormatted,
            "{cost}" to costFormatted,
            "{subtotal}" to subTotalFormatted,
            "{total}" to totalFormatted,
            "{total_text}" to total.asWords()
        ))

    }

    fun fillIkassaTariff(quantity: Short = 1, period: Short): RenderedDocument {
        val oneMonthLicense = "ikassa_license"

        val productKey = if (period == 1.toShort()) {
            oneMonthLicense
        } else {
            "ikassa_license_$period"
        }

        val license = productService.getProductByAlias(productKey) ?: productService.getProductByAlias(oneMonthLicense)
        ?: throw IllegalStateException("Cannot find license product")

        return fillIkassaTariff(license, quantity)
    }

    fun fillIkassaTariff(license: Product, quantity: Short): RenderedDocument {
        val licenseTotal = license.toTotal(quantity)

        val total = licenseTotal.total
        val totalFormatted = "$total ${license.currency}"

        val cost = licenseTotal.cost
        val costFormatted = "$cost ${license.currency}"

        val subTotal = licenseTotal.subTotal
        val subTotalFormatted = "$subTotal ${license.currency}"

        return renderDocumentFromMap("docs", documentRepository.findByAlias("invoice_license")!!, mapOf(
            "{tariff[caption]}" to license.comment,
            "{tariff[price]}" to licenseTotal.priceFormatted,
            "{tariff[tax]}" to licenseTotal.taxFormatted,
            "{tariff[cost]}" to licenseTotal.costFormatted,
            "{tariff[subtotal]}" to licenseTotal.subTotalFormatted,
            "{tariff[total]}" to licenseTotal.totalFormatted,

            "{quantity}" to licenseTotal.quantityFormatted,
            "{cost}" to costFormatted,
            "{subtotal}" to subTotalFormatted,
            "{total}" to totalFormatted,
            "{total_text}" to total.asWords()
        ))
    }

    fun fillSkkoInvoice(quantity: Short = 1) : RenderedDocument {
        val oneMonthLicense = "skko_license"

        val license6 = productService.getProductByAlias("skko_license_6")
            ?: throw IllegalStateException("Cannot find skko license product")
        val license12 = productService.getProductByAlias("skko_license_12")
            ?: throw IllegalStateException("Cannot find skko license product")
        val license6Total = license6.toTotal(quantity)
        val license12Total = license12.toTotal(quantity)
        val connection = productService.getProductByAlias("skko_register")
            ?: throw IllegalStateException("Cannot find skko registration product")

        val connectionTotal = connection.toTotal(quantity)
        return renderDocumentFromMap("docs", documentRepository.findByAlias("skko_invoice")!!, mapOf(
            "{skko_connection[caption]}" to connection.comment,
            "{skko_connection[price]}" to connectionTotal.priceFormatted,
            "{skko_connection[tax]}" to connectionTotal.taxFormatted,
            "{skko_connection[tax_sum]}" to connectionTotal.taxSumFormatted,
            "{skko_connection[tax_total]}" to connectionTotal.taxTotalFormatted,
            "{skko_connection[cost]}" to connectionTotal.costFormatted,
            "{skko_connection[total]}" to connectionTotal.totalFormatted,

            "{skko_tariff_6[caption]}" to license6.comment,
            "{skko_tariff_6[price]}" to license6Total.priceFormatted,
            "{skko_tariff_6[tax]}" to license6Total.taxFormatted,
            "{skko_tariff_6[tax_sum]}" to license6Total.taxSumFormatted,
            "{skko_tariff_6[tax_total]}" to license6Total.taxTotalFormatted,
            "{skko_tariff_6[cost]}" to license6Total.costFormatted,
            "{skko_tariff_6[total]}" to license6Total.totalFormatted,

            "{skko_tariff_12[caption]}" to license12.comment,
            "{skko_tariff_12[price]}" to license12Total.priceFormatted,
            "{skko_tariff_12[tax]}" to license12Total.taxFormatted,
            "{skko_tariff_12[tax_sum]}" to license12Total.taxSumFormatted,
            "{skko_tariff_12[tax_total]}" to license12Total.taxTotalFormatted,
            "{skko_tariff_12[cost]}" to license12Total.costFormatted,
            "{skko_tariff_12[total]}" to license12Total.totalFormatted,
            "{T12TOTAL}" to license12Total.totalFormatted,

            "{quantity}" to connectionTotal.quantityFormatted
        ))
    }

    fun fillNewContract(full: DocumentDto): RenderedDocument {
        val document = documentRepository.findByAlias("skko_contract")!!
        return RenderedDocument(document.name, fillNewContract(full, "docs/fill_auto/${document.path}"))
    }
    fun fillNewContract(full : DocumentDto, path : String) : ByteArray {
        val org = full.contractData.organizationInfo

        return renderDocument(path) { contractDocument ->
                val remap = mutableMapOf(
                    "CONTRACT_HEADER" to contractHeader(org),
                    "{company[unp]}" to org.unpN,
                    "UNP" to org.unpN,
                    "{company[organization]}" to getOrganizationName(org),
                    "ORG_NAME" to getOrganizationName(org),
                    "{company[post_address]}" to "${org.postAddress["index"]} ${org.postAddress["address"]}",
                    "POST_ADDRESS" to "${org.postAddress["index"]} ${org.postAddress["address"]}",
                    "{company[legal_address]}" to "${org.urAddress["index"]} ${org.urAddress["address"]}",
                    "LEGAL_ADDRESS" to "${org.urAddress["index"]} ${org.urAddress["address"]}",
                    "{company[email]}" to org.mail,
                    "EMAIL" to org.mail,
                    "{company[phone]}" to org.phone,
                    "PHONE" to org.phone,
                    "BANK_ACCOUNT" to full.contractData.bankInfo["payment"],
                    "BANK_BIC" to full.contractData.bankInfo["BICBank"],
                    "BANK_NAME" to full.contractData.bankInfo["nameBank"],
                    "BANK_ADDRESS" to full.contractData.bankInfo.getOrDefault("bankAddress", EMPTY_FIELD),
                )

                remap.putAll(if(org.orgF == "IP") {
                    /** если фио клиента и имя орги совпадают и клиент ип, применяем спецправила по заполнению полей */
                    val (orgNameIp, fioClientIp, powerPaperIp) = if(org.fioClient == stripIp(org)) {
                        listOf("${getOrganizationName(org)} / ${powerPaperText(org)}", "", "")
                    } else {
                        listOf(getOrganizationName(org), org.fioClient, powerPaperText(org))
                    }
                    mapOf(
                        "FIOCLIENTIP" to fioClientIp,
                        "ORGNAMEIP" to orgNameIp,
                        "POWERPAPERIP" to powerPaperIp,
                        "FIOCLIENTUR" to "",
                        "ORGNAMEUR" to "",
                        "POWERPAPERUR" to ""
                    )
                } else {
                    mapOf(
                        "FIOCLIENTIP" to "",
                        "ORGNAMEIP" to "",
                        "POWERPAPERIP" to "",
                        "FIOCLIENTUR" to "${org.fioClient}, ${org.positionClient}",
                        "ORGNAMEUR" to getOrganizationName(org),
                        "POWERPAPERUR" to powerPaperText(org)
                    )
                })
                contractDocument.replaceMultiple(remap)
        }
    }

    fun fillExistingContract(full: DocumentDto): RenderedDocument {
        val document = documentRepository.findByAlias("skko_contract_application")!!
        return RenderedDocument(document.name, fillExistingContract(full, "docs/fill_auto/${document.path}"))
    }

    fun fillExistingContract(full: DocumentDto, path: String): ByteArray {
        val org = full.contractData.organizationInfo

        return renderDocument(path) { contractDocument ->
            contractDocument.replaceMultiple(mapOf(
                "CONTRACT_HEADER" to contractHeader(org),
                "{company[unp]}" to org.unpN,
                "UNP" to org.unpN,
                "{company[organization]}" to getOrganizationName(org),
                "ORG_NAME" to getOrganizationName(org),
                "{company[post_address]}" to "${org.postAddress["index"]} ${org.postAddress["address"]}",
                "{company[legal_address]}" to "${org.urAddress["index"]} ${org.urAddress["address"]}",
                "{company[email]}" to org.mail,
                "{company[phone]}" to org.phone,
                "COMPANY_EMAIL" to org.mail,
                "POST_ADDRESS" to "${org.postAddress["index"]} ${org.postAddress["address"]}",
                "LEGAL_ADDRESS" to "${org.urAddress["index"]} ${org.urAddress["address"]}",
                "BANK_ACCOUNT" to full.contractData.bankInfo["payment"],
                "BANK_BIC" to full.contractData.bankInfo["BICBank"],
                "BANK_NAME" to full.contractData.bankInfo["nameBank"],
                "BANK_ADDRESS" to full.contractData.bankInfo.getOrDefault("bankAddress", EMPTY_FIELD),
            ))
            /*full.contractData.tradeInfo.forEachIndexed { index, tradepoint ->
                val tabRow = XWPFTableRow(
                    contractDocument.tables[contractDocument.tables.lastIndex].ctTbl.addNewTr(),
                    contractDocument.tables[contractDocument.tables.lastIndex]
                )
                val numCell = tabRow.createCell()
                numCell.text = (index + 1).toString()
                val addressCell = tabRow.createCell()
                addressCell.text = tradepoint.torgAddress
                val koCountCell = tabRow.createCell()
                koCountCell.text = "-"//tradepoint.unitsCashbox.toString()
                val ikassaCountCell = tabRow.createCell()
                ikassaCountCell.text = tradepoint.unitsCashbox.toString()
                val modelTextCell = tabRow.createCell()
                modelTextCell.text = "ПК ${getSolutionName(full.equipment.getValue("solution"))}"
                val usCell = tabRow.createCell()
                usCell.text = "ООО «АЙЭМЛЭБ»"
            }*/
        }
    }

    fun fillSkoAct(org: OrganizationInfoDto, quantity: Short = 1): RenderedDocument {
        val connection = productService.getProductByAlias("ikassa_register")
            ?: throw IllegalStateException("Cannot find ikassa registration product")
        val connectionTotal = connection.toTotal(1)
        val document = documentRepository.findByAlias("sko_act")!!
        return RenderedDocument(document.name, renderDocument("docs/fill_auto/${document.path}") { skoActDocument ->
            skoActDocument.replaceMultiple(mapOf(
                "{company[organization]}" to getOrganizationName(org),
                "{company[unp]}" to org.unpN
            ))

            for (i in 0 until quantity) {
                val tabRow = XWPFTableRow(skoActDocument.tables[skoActDocument.tables.lastIndex - 1].ctTbl.addNewTr(), skoActDocument.tables[skoActDocument.tables.lastIndex - 1])
                val numCell = tabRow.createCell()
                numCell.text = (i + 1).toString()
                numCell.makeBorder()
                val serialCell = tabRow.createCell()
                serialCell.text = ""
                serialCell.makeBorder()
                val modelCell = tabRow.createCell()
                modelCell.text = "AvTPCR 128-F-01"
                modelCell.makeBorder()
            }
        })
    }

    fun fillApplication(full: DocumentDto) : RenderedDocument {
        val organization = full.contractData.organizationInfo
        val trade = full.contractData.tradeInfo
        val document = documentRepository.findByAlias("skko_connection_application")!!

        return RenderedDocument(document.name, renderDocument("docs/skko/dummy_no_margins.docx") { newDocument->
            newDocument.removeBodyElement(0)

            trade.forEachIndexed { tradePointIndex,tradePoint ->
                val tradePointWorkTime =tradePoint.strTimeWork.joinToString (separator = "; ")
                val tradePointWorkTimeFontSize = countFontSizeByStringLength(tradePointWorkTime)
                val legalAddress = "${organization.urAddress["index"]} ${organization.urAddress["address"]}"
                val postAddress = "${organization.postAddress["index"]} ${organization.postAddress["address"]}"
                val postAddressFontSize = countFontSizeByStringLength(postAddress)

                val tradePointAddress = tradePoint.torgAddress
                val tradePointAddressFontSize = countFontSizeByStringLength(tradePointAddress)
                for (i in 0 until tradePoint.unitsCashbox) {
                    getFile("docs/fill_auto/${document.path}").use { docInputStream ->
                        XWPFDocument(docInputStream).use { doc ->
                            val paragraph: XWPFParagraph = doc.createParagraph()
                            val run = paragraph.createRun()
                            run.fontSize = 1
                            run.setText("-")
                            paragraph.isPageBreak = true

                            if (tradePointIndex + 1 == trade.count() && i + 1 == tradePoint.unitsCashbox) {
                                doc.removeBodyElement(doc.bodyElements.lastIndex)
                            }
                            val glnToggle = if(!tradePoint.glnToggle.isNullOrBlank() && tradePoint.glnToggle != "false") { "Да" } else {
                                if(tradePoint.glnNumber.isNullOrBlank()) "Нет" else "Да"
                            }
                            val glnNumber = if(!tradePoint.glnNumber.isNullOrBlank()) {
                                tradePoint.glnNumber
                            } else {
                                ""
                            }
                            val solution = solutionService.getSolutionByAlias(full.equipment.getOrDefault("solution", "smart"))!!
                            doc.replaceMultiple(mapOf(
                                "{company[unp]}" to organization.unpN,
                                "{company[organization]}" to getOrganizationName(organization),
                                "{company[post_address]}" to postAddress,
                                "{company[email]}" to organization.mail,
                                "{company[phone]}" to organization.phone,
                                "{tradepoint[type]}" to tradePoint.torgType,
                                "{tradepoint[name]}" to tradePoint.torgName,
                                "{tradepoint[address]}" to tradePointAddress,
                                "{tradepoint[worktime]}" to tradePointWorkTime,
                                "TRADEPOINT_GLN_TOGGLE" to glnToggle,
                                "TRADEPOINT_GLN" to glnNumber,
                                "{solution}" to solution.legalName,
                                "SOLUTION" to solution.legalName,
                                "VERSION" to solution.version
                            ))
                            doc.tables.forEach {tbl ->
                                tbl.rows.forEach { row ->
                                    if (row.tableCells.count() >= 3) {
                                        row.tableCells[2].paragraphs.forEach { parag ->
                                            if (parag.text == tradePointWorkTime) {
                                                parag.runs.forEach { it.fontSize = tradePointWorkTimeFontSize }
                                            }

                                            if (parag.text == postAddress) {
                                                parag.runs.forEach { it.fontSize = postAddressFontSize }
                                            }

                                            if (parag.text == tradePointAddress) {
                                                parag.runs.forEach { it.fontSize = tradePointAddressFontSize }
                                            }
                                        }
                                    }
                                }
                            }

                            fillOneDocumentFromAnother(doc, newDocument)
                        }
                    }
                }
            }
        })
    }

    private fun fillOneDocumentFromAnother(source: XWPFDocument, target: XWPFDocument) {
        source.bodyElements.forEachIndexed { index, element ->
            if (element.elementType == BodyElementType.PARAGRAPH) {
                val p = target.createParagraph()
                val _index = target.paragraphs.indexOf(p)
                target.setParagraph(element as XWPFParagraph, _index)
            }
            if (element.elementType == BodyElementType.TABLE) {
                val t = target.createTable()
                val _index = target.tables.indexOf(t)
                target.setTable(_index, (element as XWPFTable))
            }
        }
    }

    fun fillNotification(organization: OrganizationInfoDto) : RenderedDocument {
        return renderDocumentFromMap(
            "docs/fill_auto",
            documentRepository.findByAlias("connection_notification")!!,
            mapOf("NOTIFICATION_HEADER" to notificationHeader(organization)))
    }

    fun fillLkUnsafe(organization: OrganizationInfoDto) : RenderedDocument {
        return renderDocumentFromMap(
            "docs/fill_auto",
            documentRepository.findByAlias("declaration_lk_unsafe")!!,
            mapOf("NOTIFICATION_HEADER" to notificationHeader(organization),
                "UNP" to organization.unpN)
        )
    }

    fun fillInstruction(contract : Boolean? = null, solution : Solution) : String {
        val contractText = if (contract == null) {
            """
Два экземпляра заполненного и подписанного со своей стороны Заявления о присоединении к Публичному договору СККО либо дополнительного соглашения с РУП "Информационно-издательский центр по налогам и сборам"
<ul>
    <li>Если у Вас договор на СКНО НЕ заключен, то «01-1 Заявление о присоединении к Публичному договору СККО»;</li>
    <li>Если у Вас договор на СКНО заключен, то «01-2 Дополнительное соглашение»;</li>
</ul>
"""
        } else if(contract) {
            """
Два экземпляра заполненного и подписанного со своей стороны дополнительного соглашения
с РУП “Информационно-издательский центр по налогам и сборам“:
«01-2 Дополнительное соглашение»;
"""
        } else {
            """
Два экземпляра заполненного и подписанного со своей стороны Заявления о присоединении к Публичному договору СККО:
«01-1 Заявление о присоединении к Публичному договору СККО»
"""
        }
        val equipment = solution.equipment.map { it.name }
        val contentsName = solution.contents.map { it.name }




        throw NotImplementedError()
    }

    fun powerPaperText(org: OrganizationInfoDto): String {
        val DOC_NUMBER_FIELD = org.docValue?.get("DOC_NUMBER_FIELD")
        val DATE_FIELD = org.docValue?.get("DATE_FIELD")
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
        return "${Doc.getById(org.docType).value} $numField $dateField"
    }

    fun getDocumentByAlias(alias : String) : Document? {
        return documentRepository.findByAlias(alias)
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

    private fun countFontSizeByStringLength(string: String): Int {
        return if (string.length > 70) {
            6
        } else if (string.length > 45) {
            8
        } else if (string.length > 35) {
            9
        } else {
            11
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

    fun renderDocument(_basePath: String, document: Document): RenderedDocument {
        val basePath = if (_basePath.endsWith("/")) {
            _basePath
        } else {
            "$_basePath/"
        }
        return RenderedDocument(
            document.name,
            getFileAsByteArray(
                "$basePath${document.path}"
            ) ?: throw IllegalArgumentException("$basePath${document.path} does not exists.")
        )
    }

    fun renderDocument(_basePath: String, accompanyingDoc: AccompanyingDoc) : RenderedDocument {
        val basePath = if (_basePath.endsWith("/")) {
            _basePath
        } else {
            "$_basePath/"
        }
        return RenderedDocument(
            accompanyingDoc.name,
            getFileAsByteArray("$basePath${accompanyingDoc.path}"
            ) ?: throw IllegalArgumentException("$basePath${accompanyingDoc.path} does not exists.")
        )
    }


    fun renderDocumentFromMap(_basePath: String, document: Document, map: Map<String, String?>): RenderedDocument {
        val basePath = if (_basePath.endsWith("/")) {
            _basePath
        } else {
            "$_basePath/"
        }
        return RenderedDocument(
            document.name,
            renderDocument(
                "$basePath${document.path}"
            ) {
                it.replaceMultiple(map)
            }
        )
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

    private fun contractHeader(org: OrganizationInfoDto): String {
        val oe = if (org.orgF == "IP") {
            "ый"
        } else {
            "ое"
        }
        return "${getOrganizationName(org)}, именуем${oe} в дальнейшем «Пользователь»," +
                " в лице ${org.positionClient2.toLowerCase()} ${org.fioClient2}, действующего на основании ${powerPaperText2(org)}"
    }

    private fun hardwareContractHeader(organiztion: OrganizationInfoDto): String {
        return contractHeader(organiztion).replace("Пользователь", "Покупатель")
    }

    private fun notificationHeader(org: OrganizationInfoDto): String {
        return "${getOrganizationName(org)} в лице ${org.positionClient2.toLowerCase()} ${org.fioClient2}," +
                " действующего на основании ${powerPaperText2(org)}"
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

    enum class Doc(val id: Int, val value: String) {
        EMPTY(0, ""),
        CHARTER(1, "Устав"),
        CERTIFICATE(2, "Свидетельство о гос. регистрации"),
        DOC(3, "Устав и договор"),
        ATTORNEY(4, "Доверенность"),
        CONTRACT(5, "Договор"),
        ORDER(6, "Приказ");

        companion object {
            fun getById(id: Int): Doc {
                return values()
                    .find { it.id == id } ?: throw IllegalStateException("Unknown status with id $id")
            }

            fun from(id: Int): Doc {
                return values()
                    .find { it.id == id } ?: throw IllegalStateException("Unknown doc with id $id")
            }
        }
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

