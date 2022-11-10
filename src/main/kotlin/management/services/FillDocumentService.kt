package management.services

import jakarta.inject.Singleton
import management.controllers.DocumentController
import management.data.docs.Document
import management.data.docs.RenderedDocument
import management.data.products.AccompanyingDoc
import management.data.products.Product
import management.data.products.ProductTotal
import management.data.products.Solution
import management.data.repositories.DocumentRepository
import management.data.utils.Util
import management.data.utils.UtilsRepository
import management.forms.DocumentDto
import management.forms.FancyMailBodyFragmentDto
import management.forms.OrganizationInfoDto
import management.utils.*
import org.apache.poi.xwpf.usermodel.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.zip.ZipOutputStream


@Singleton
class FillDocumentService (
    private val documentRepository: DocumentRepository,
    private val utilRepository : UtilsRepository,
    private val productService: ProductService,
    private val solutionService: SolutionService,
    private val templateService: TemplateService
    ) {

    private val EMPTY_FIELD = "______________________________________"


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

        val pew = full?.equipment?.getOrDefault("bank", null)
            ?: solution?.extraVars?.getOrDefault("PROCESSINGPROVIDER", null)
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

        val license = productService.getProductByAlias(productKey)
            ?: productService.getProductByAlias(oneMonthLicense)
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
    private fun fillNewContract(full : DocumentDto, path : String) : ByteArray {
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

    private fun fillExistingContract(full: DocumentDto, path: String): ByteArray {
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
            utilRepository.findByName("contract_null")?.data
                ?: throw IllegalStateException("cannot find default contract text")
        } else if(contract) {
            utilRepository.findByName("contract")?.data
                ?: throw IllegalStateException("cannot find default contract text")
        } else {
            utilRepository.findByName("not_contract")?.data
                ?: throw IllegalStateException("cannot find default contract text")
        }
        val equipment = solution.equipment.map { it.name }
        val contentsName = solution.contents.map { it.name }

        val licenseContent = if ((
                    contentsName.containsAll(listOf("ikassa_register", "ikassa_license")) ||
                    contentsName.containsAll(listOf("ikassa_register", "ikassa_license_12_season"))
                    ) || solution.contents.isEmpty()) {
            utilRepository.findByName("empty_content")?.data
                ?: throw IllegalStateException("cannot find default content for ikassa_register and ikassa_license")
        } else if (
            contentsName.contains("ikassa_license")
            || contentsName.contains("ikassa_license_12_season")
        ) {
            utilRepository.findByName("ikassa_license")?.data
                ?: throw IllegalStateException("cannot find default content for ikassa_license")

        } else if (contentsName.contains("ikassa_register")) {
            utilRepository.findByName("ikassa_register")?.data
                ?: throw IllegalStateException("cannot find default content for ikassa_register")
        } else {
            " "
        }

        val message = FancyMailBodyFragmentDto(
            vars = hashMapOf(
                "{body}" to FancyMailBodyFragmentDto(
                    vars=hashMapOf(
                        "{contract}" to contractText,
                        "{ikassa_license_text}" to licenseContent,
                        "{equipment}" to if(equipment.isEmpty()) " " else equipment.joinToString(prefix="<li>", separator="</li><li>", postfix = "</li>")
                    ),
                    templateFile = "pages/instruction"
                )
            ),
            templateFile = "email/email-text-block"
        )

        val fullMessage = FancyMailBodyFragmentDto(
            vars = hashMapOf(
                "{header_logo}" to "https://ikassa.by/logo_email_big.jpg",
                "{header}" to utilRepository.findByName("thanks_header")!!.data,
                "{message}" to message,
                "{footer}" to utilRepository.findByName("footer")!!.data
            ),
            templateFile = "email/email"
        )
        return templateService.renderFancyMessageFragment(fullMessage)
    }

    fun step3Common(
        period : Short,
        count: Short,
        fullData : DocumentDto? = null,
        solution : Solution = solutionService.getSolutionByAlias("smart")!!
    ) : MutableList<RenderedDocument> {


        val equipment = productService.getAllProducts().mapNotNull {
            val isProduct = solution.equipment.contains(productService.getProductByAlias(it.alias))
            if(
                isProduct &&
                period >= 6.toShort() &&
                listOf("pax930", "pax930_lancard", "pax930_promo").contains(it.alias)
            ) {
                it.copy(price=productService.getProductByAlias("pax930_promo")!!.price)
            } else if(isProduct) {
                it
            } else {
                null
            }
        }

        val solutionContent = solution.contents.map { it.alias }
        val renderedDocuments = mutableListOf<RenderedDocument>()

        val billingMode = if (solutionContent.isEmpty() || solutionContent.containsAll(listOf("ikassa_register", "ikassa_license"))) {
            DocumentController.IkassaBillingMode.FULL
        } else if (solutionContent.contains("ikassa_register")) {
            DocumentController.IkassaBillingMode.REGISTER
        } else {
            DocumentController.IkassaBillingMode.LICENSE
        }

        solution.contents.forEach {
            when(it.alias) { "ikassa_register" -> {
                when(billingMode) { DocumentController.IkassaBillingMode.FULL -> {
                    val ikassaInvoice = this.fillIkassaInvoice(
                        count,
                        period,
                        if (solutionContent.contains("dusik_r")) {
                            "_dusik"
                        }
                        else {
                            ""
                        }
                    )
                    ikassaInvoice.name = "${ikassaInvoice.name} за $period месяц${period.morph("", "а", "ев")}"
                    renderedDocuments.add(ikassaInvoice)
                }

                    DocumentController.IkassaBillingMode.LICENSE -> {
                        val ikassaInvoice = this.fillIkassaRegistration(count)
                        renderedDocuments.add(ikassaInvoice)
                    }

                    DocumentController.IkassaBillingMode.REGISTER -> {
                        val ikassaInvoice = this.fillIkassaTariff(count, period)
                        ikassaInvoice.name = "${ikassaInvoice.name} за $period месяц${period.morph("", "а", "ев")}"
                        renderedDocuments.add(ikassaInvoice)
                    }

                }


            }
                "ikassa_license_12_season" -> {
                    val licenseProduct = productService.getProductByAlias(it.alias!!)!!
                    val ikassaInvoice = this.fillIkassaTariff(licenseProduct, count)
                    ikassaInvoice.name = "${ikassaInvoice.name} за $period месяц${period.morph("", "а", "ев")}"
                    renderedDocuments.add(ikassaInvoice)
                }
                "dusik_r" -> {
                    // FIXME
                }

                "skko_register" -> {
                    renderedDocuments.add(this.fillSkkoInvoice(count))
                }

            }
        }


        renderedDocuments.add(this.renderDocument("docs",
            this.getDocumentByAlias("attorney")!!
        ))
        renderedDocuments.addAll(
            this.fillProductsDocuments(equipment, count, fullData, solution)
        )
        return renderedDocuments
    }


    fun dateNumFields(organization: OrganizationInfoDto) : Map<String, String> {
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
        return mapOf(
            "numField" to numField,
            "dateField" to dateField
        )
    }
    fun powerPaperText(org: OrganizationInfoDto): String {
        val fields = dateNumFields(org)
        return "${Doc.getById(org.docType).value} ${fields["numField"]} ${fields["dateField"]}"
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
        val basePath = _basePath.trimEnd('/')
        val path = "$basePath${document.path}"
        return RenderedDocument(
            document.name,
            renderDocument(path) {
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
        val fields = dateNumFields(organization)
        return "${DocWithEnding.getById(organization.docType).value} ${fields["numField"]} ${fields["dateField"]}"
    }

    private fun contractHeader(org: OrganizationInfoDto): String  {
        val oe = if (org.orgF == "IP") {
            "ый"
        } else {
            "ое"
        }
        return "${getOrganizationName(org)}, именуем${oe} в дальнейшем «Пользователь»," +
                " в лице ${org.positionClient2.toLowerCase()} ${org.fioClient2}, действующего на основании ${powerPaperText2(org)}"
    }

    private fun hardwareContractHeader(organization: OrganizationInfoDto): String {
        return contractHeader(organization).replace("Пользователь", "Покупатель")
    }

    private fun notificationHeader(org: OrganizationInfoDto): String {
        return "${getOrganizationName(org)} в лице ${org.positionClient2.toLowerCase()} ${org.fioClient2}," +
                " действующего на основании ${powerPaperText2(org)}"
    }

    fun exportDefaultDocs() : List<Document> {
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

    fun exportDefaultUtils() : List<Util> {
        return utilRepository.saveAll(
            listOf(
                Util(
                name = "contract_null",
                data =
                """
Два экземпляра заполненного и подписанного со своей стороны Заявления о присоединении к Публичному договору СККО либо дополнительного соглашения с РУП "Информационно-издательский центр по налогам и сборам"
<ul>
    <li>Если у Вас договор на СКНО НЕ заключен, то «01-1 Заявление о присоединении к Публичному договору СККО»;</li>
    <li>Если у Вас договор на СКНО заключен, то «01-2 Дополнительное соглашение»;</li>
</ul>
"""
                ),
                Util(
                    name = "contract",
                    data =
                    """
Два экземпляра заполненного и подписанного со своей стороны дополнительного соглашения
с РУП “Информационно-издательский центр по налогам и сборам“:
«01-2 Дополнительное соглашение»;
"""

                ),
                Util(
                    name = "not_contract",
                    data =
                    """
Два экземпляра заполненного и подписанного со своей стороны Заявления о присоединении к Публичному договору СККО:
«01-1 Заявление о присоединении к Публичному договору СККО»
"""
                ),
                Util(
                    name = "empty_content",
                    data =  "<li>Регистрация программной кассы в АИС ПКС IKASSA и абонентское обслуживание программной кассы;</li>"
                ),
                Util(
                    name = "ikassa_register",
                    data = "<li>Регистрация программной кассы в АИС ПКС IKASSA;</li>"
                ),
                Util(
                    name = "ikassa_license",
                    data = "<li>Абонентское обслуживание программной кассы IKASSA;</li>"
                ),
                Util(
                    name = "thanks_header",
                    data ="""
Благодарим вас за выбор программной кассы iKassa
"""
                ),
                Util(
                    name = "footer",
                    data = """
Подписанный пакет документов с копиями платежек и заверенной печатью организации, не нотариально, 
копию свидетельства о государственной регистрации субъекта хозяйствования 
(при условии, указанном ранее) необходимо подать либо направить почтовым отправлением по адресу: 
<br/>
ООО «АЙЭМЛЭБ», 220002, г. Минск, ул. Сторожевская, д.8, пом. 205/2.
"""
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

