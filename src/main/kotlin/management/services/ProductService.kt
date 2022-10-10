package management.services

import io.micronaut.json.tree.JsonArray
import io.micronaut.json.tree.JsonNode
import jakarta.inject.Singleton
import management.entities.AccompanyingDoc
import management.entities.Product
import management.links.entities.ProductAccDoc
import management.links.repositories.ProductAccDocRepository
import management.repositories.AccompanyingDocRepository
import management.repositories.ProductRepository
import management.utils.FilePath.PATH_TO_ADAPTER_MICROUSB
import management.utils.FilePath.PATH_TO_ADAPTER_MICROUSB_WHIPPY
import management.utils.FilePath.PATH_TO_ADAPTER_TYPEC
import management.utils.FilePath.PATH_TO_AZUR8223
import management.utils.FilePath.PATH_TO_AZUR8223_BELVTI
import management.utils.FilePath.PATH_TO_AZUR_FM
import management.utils.FilePath.PATH_TO_AZUR_FM_BELVTI
import java.math.BigDecimal
import management.utils.FilePath.PATH_TO_FM
import management.utils.FilePath.PATH_TO_NEXGO
import management.utils.FilePath.PATH_TO_PAX910
import management.utils.FilePath.PATH_TO_PAX930
import management.utils.FilePath.PATH_TO_PAX930_BAG
import management.utils.FilePath.PATH_TO_PRINTER_RPP02A
import management.utils.FilePath.PATH_TO_PRINTER_RPP02N


@Singleton
class ProductService (private val productRepository: ProductRepository,
                      private val accompanyingDocRepository: AccompanyingDocRepository,
                      private val productAccDocLinkRepository: ProductAccDocRepository) {

    private fun makeProducts(productData: JsonArray) : MutableList<Product>? {
        val productList : MutableList<Product> = mutableListOf()
        (0 until productData.size()).forEach {
            val product : JsonNode = productData.get(it) ?: return null
            var docs : MutableList<AccompanyingDoc> = mutableListOf()
            if (product["accompanying_docs"] != null) {
                 docs = makeAccompanyingDocs(product.get("accompanying_docs") as JsonArray)
            }
            productList += Product(
                alias = product.get("alias")!!.stringValue,
                name = product.get("name")!!.stringValue,
                comment = product.get("comment")?.stringValue,
                price = product.get("price")!!.bigDecimalValue, // "price":123.4
                tax = product.get("tax")?.bigDecimalValue ?: BigDecimal.ZERO,
                currency = product.get("currency")?.stringValue,
                units = product.get("units")?.intValue ?: 1,
                roundTotal = product.get("round_total")?.booleanValue ?: false, // "round_total":true
                dualDocs = product.get("dual_docs")?.booleanValue ?: false, // "dual_docs":true
                accompanyingDocs = docs
            )
        }
        return productList
    }

    private fun makeProduct(product : JsonNode, docs : MutableList<AccompanyingDoc>) : Product {

        return Product(
                alias = product.get("alias")!!.stringValue,
                name = product.get("name")!!.stringValue,
                comment = product.get("comment")?.stringValue,
                price = product.get("price")!!.bigDecimalValue, // "price":123.4
                tax = product.get("tax")?.bigDecimalValue ?: BigDecimal.ZERO,
                currency = product.get("currency")?.stringValue,
                units = product.get("units")?.intValue ?: 1,
                roundTotal = product.get("round_total")?.booleanValue ?: false, // "round_total":true
                dualDocs = product.get("dual_docs")?.booleanValue ?: false, // "dual_docs":true
                accompanyingDocs = docs
            )


    }

    private fun makeAccompanyingDocs(docs : JsonArray) : MutableList<AccompanyingDoc> {
        val accompanyingDocList : MutableList<AccompanyingDoc> = mutableListOf()
        (0 until docs.size()).forEach {
            val doc : JsonNode = docs.get(it)!!
            accompanyingDocList += AccompanyingDoc(
                path = doc.get("path")!!.stringValue,
                name = doc.get("name")!!.stringValue,
                raw = doc.get("raw")?.booleanValue ?: false
            )
        }
        return accompanyingDocList
    }

    fun checkAccompanyingDocs(products : JsonArray) : MutableList<Product> {
        val savedProducts : MutableList<Product> = mutableListOf()
        println(products)
        (0 until products.size()).forEach {
            val product = products.get(it)!!
            if (product["accompanying_docs"] == null) {
                savedProducts.add(productRepository.save(makeProduct(product, mutableListOf<AccompanyingDoc>())))
                return@forEach
            }
            val accompanyingDocList = makeAccompanyingDocs(product["accompanying_docs"] as JsonArray)
            val accompanyingDocListCopy = accompanyingDocList
            val savedAccompanyingDocList : MutableList<AccompanyingDoc> = mutableListOf()
            (0 until accompanyingDocList.size).forEach {
                val doc = accompanyingDocList[it]
                println(1)
                if (accompanyingDocRepository.findByPath(doc.path) != null) {
                    accompanyingDocListCopy.remove(doc)
                    savedAccompanyingDocList.add(accompanyingDocRepository.findByPath(doc.path)!!)
                }
            }

            val savedProduct = productRepository.save(makeProduct(product, accompanyingDocListCopy))
            for (doc in savedAccompanyingDocList) {
                val link = productAccDocLinkRepository.save(
                    ProductAccDoc(
                        productId = savedProduct.productId,
                        accDocId = doc.accompanyingDocId))
            }
            savedProducts.add(savedProduct)
        }
        return savedProducts;
    }

    fun getAllProducts() : MutableList<Product> {
        return productRepository.findAll()
    }

    fun getProductByAlias(alias : String) : MutableList<Product> {
        return productRepository.findByAlias(alias)
    }

    fun createProduct(productData : JsonArray) : MutableList<Product> {
        return this.checkAccompanyingDocs(productData)
    }

    fun updateProductName(alias: String, name : Map<String, String>) {
        return productRepository.updateByAlias(alias, name["name"]!!)
    }

    fun updateProductPrice(alias: String, price : Map<String, String>) {
        return productRepository.updateByAlias(alias, price["price"]!!.toBigDecimal())
    }

    fun updateProductTax(alias : String, tax : Map<String, String>) {
        return productRepository.updateByAlias(
            alias,
            tax.getOrDefault("tax", BigDecimal.ZERO)
            .toString()
            .toBigDecimal())
    }

    fun deleteProduct(alias : String) {
        return productRepository.deleteByAlias(alias)
    }


    fun exportDefault() : List<Product> {
        return productRepository.saveAll(listOf(

                Product(alias = "ikassa_register",
                        name = "Регистрация программной кассы (включая СКО)", comment = "Регистрация Программной кассы (ПК) в АИС ПКС iKassa",
                        price = BigDecimal(53)),
                Product(alias = "ikassa_register_only",
                        name = "Регистрация программной кассы (включая СКО)", comment = "Регистрация Программной кассы (ПК) в АИС ПКС iKassa",
                        price = BigDecimal(53)),
                Product(alias = "ikassa_license",
                        name = "Абонентское обслуживание iKassa", comment = "Абонентская плата (за 1 месяц)",
                        price = BigDecimal(15)),
                Product(alias = "ikassa_license_3", //ikassa license 3
                        name = "Абонентское обслуживание iKassa", comment = "Абонентская плата (за 3 месяца)",
                        price = BigDecimal(45)),
                Product(alias = "ikassa_license_6",
                        name = "Абонентское обслуживание iKassa", comment = "Абонентская плата (за 6 месяцев)",
                        price = BigDecimal(87)),//BigDecimal(14.5)),
                Product(alias = "ikassa_license_12",
                        name = "Абонентское обслуживание iKassa", comment = "Абонентская плата (за 12 месяцев)",
                        price = BigDecimal(174)),//BigDecimal(14.5)),
                Product(alias = "ikassa_license_dusik",
                        name = "Абонентское обслуживание iKassa", comment = "Абонентская плата (за 1 месяц)",
                        price = BigDecimal(20)),
                Product(alias = "ikassa_license_3_dusik", //ikassa license 3
                        name = "Абонентское обслуживание iKassa", comment = "Абонентская плата (за 3 месяца)",
                        price = BigDecimal(60)),
                Product(alias = "ikassa_license_6_dusik",
                        name = "Абонентское обслуживание iKassa", comment = "Абонентская плата (за 6 месяцев)",
                        price = BigDecimal(120)),//BigDecimal(14.5)),
                Product(alias = "ikassa_license_12_dusik",
                        name = "Абонентское обслуживание iKassa", comment = "Абонентская плата (за 12 месяцев)",
                        price = BigDecimal(240)),//BigDecimal(14.5)),
                Product(alias = "skko_register",
                        name = "Регистрация программной кассы в СККО", comment = "",
                        price = BigDecimal(2.50),
                        tax = BigDecimal(20)),
                Product(alias = "skko_license_6",
                        name = "Оплата за обслуживание программной кассы в СККО", comment = "Информационное обслуживание пользователя программной кассы за 6 месяцев",
                        price = BigDecimal(25),
                        tax = BigDecimal(20)),
                Product(alias = "skko_license_12",
                        name = "Оплата за обслуживание программной кассы в СККО", comment = "Информационное обслуживание пользователя программной кассы за 12 месяцев",
                        price = BigDecimal(50),
                        tax = BigDecimal(20)),

                Product(alias = "personal",
                        name = "Личный Кабинет",
                        price = BigDecimal.ZERO),

                Product(alias = "app",
                        name = "Программная Касса", comment = "само приложение",
                        price = BigDecimal.ZERO),

                Product(alias = "fm_paymob",
                        name = "Мобильный компьютер PayMob-M1", comment = "iKassa FM",
                        price = BigDecimal(315), tax = BigDecimal(20),
                        accompanyingDocs = mutableListOf(AccompanyingDoc(PATH_TO_FM, "05 Реквизиты для оплаты устройства PayMob-M1"))),

                Product(alias = "pax930",
                        name = "POS-терминал PAX A930", comment = "iKassa Smart&Card",
                        price = BigDecimal(715), tax = BigDecimal(20),
                        dualDocs=true,
                        accompanyingDocs = mutableListOf(
                                AccompanyingDoc(PATH_TO_PAX930, "05 Счёт на оборудование РАХ А930"),
                                AccompanyingDoc("docs/static/pax930_instalment_3.docx", "05_3 Рассрочка на оборудование РАХ А930 (3 мес)"),
                                AccompanyingDoc("docs/static/pax930_instalment_6.docx", "05_6 Рассрочка на оборудование РАХ А930 (6 мес)")
                        )
                ),
                Product(alias = "pax910",
                        name = "POS-терминал PAX A910", comment = "iKassa Smart&Card",
                        price = BigDecimal(791.67)/*.toFixed(2)*/, tax = BigDecimal(20),
                        dualDocs=true,
                        accompanyingDocs = mutableListOf(AccompanyingDoc(PATH_TO_PAX910, "05 Договор на оборудование РАХ А910"))),

                Product(alias = "fm_azur",
                        name = "Кассовый аппарат «AZUR POS», модель KS8223SK", comment = "iKassa FM Azur",
                        price = BigDecimal(424.16)/*.toFixed(2)*/, tax = BigDecimal(20), roundTotal = true,
                        dualDocs=true,
                        accompanyingDocs = mutableListOf(AccompanyingDoc(PATH_TO_AZUR_FM, "05 Реквизиты для оплаты устройства FM Azur"))),

                Product(alias="adapter_typec", name = "Адаптер USB-USB Type-C",
                        price = BigDecimal(7.0),
                        accompanyingDocs = mutableListOf(AccompanyingDoc(PATH_TO_ADAPTER_TYPEC, name = "Адаптер USB-USB Type-C"))),

                Product(alias="adapter_microusb", name = "Адаптер USB-MicroUSB",
                        price = BigDecimal(7.0),
                        accompanyingDocs = mutableListOf(AccompanyingDoc(PATH_TO_ADAPTER_MICROUSB, name = "Адаптер USB-MicroUSB"))),

                Product(alias="adapter_microusb_whippy", name = "Адаптер гибкий USB-MicroUSB",
                        price = BigDecimal(8.0),
                        accompanyingDocs = mutableListOf(AccompanyingDoc(PATH_TO_ADAPTER_MICROUSB_WHIPPY, name = "Адаптер гибкий USB-MicroUSB"))),

                Product(alias = "rpp02n", name = "Мобильный принтер RPP02N", comment = "Чёрный принтер",
                        price = BigDecimal(100), tax = BigDecimal(20),
                        dualDocs=true,
                        accompanyingDocs = mutableListOf(AccompanyingDoc(PATH_TO_PRINTER_RPP02N, "05 Реквизиты для оплаты мобильного принтера"))),

                Product(name = "Мобильный принтер RPP02A", comment = "С рыжими кнопками",
                        price = BigDecimal(100), tax = BigDecimal(20),
                        dualDocs=true,
                        accompanyingDocs = mutableListOf(AccompanyingDoc(PATH_TO_PRINTER_RPP02A, "05 Реквизиты для оплаты мобильного принтера"))),

                Product(name = "Сумка-чехол с ремешком", comment = "Для PAX 930 (smart&card)",
                        price = BigDecimal(55),
                        accompanyingDocs = mutableListOf(AccompanyingDoc(PATH_TO_PAX930_BAG, "Сумка-чехол"))),

                Product(alias = "azur8223",
                        name = "Бесконтактный POS-терминал AZUR POS, модель KS 8223, с ПО", comment = "iKassa Smart&Card Azur",
                        price = BigDecimal(958.33)/*.toFixed(2)*/, tax = BigDecimal(20), roundTotal = true,
                        dualDocs=true,
                        accompanyingDocs = mutableListOf(AccompanyingDoc(PATH_TO_AZUR8223, "05 Договор на оборудование AZUR POS 8223"))),

                Product(alias = "gandlarok_mpos",
                        name = "Считыватель Vi 218 с программным протоколом взаимодействия с программным обеспечением «ГандлярОК» (магнитная полоса, чип, бесконтакт)", comment = "Гандлярок MPOS",
                        price = BigDecimal(250), tax = BigDecimal(20),
                        accompanyingDocs = mutableListOf(
                                AccompanyingDoc("docs/products/gandlarok_invoice.xlsm", "10 Счёт ГандлярОК", raw=true),
                                AccompanyingDoc("docs/products/gandlarok_doc.docx", "11 Договор купли-продажи Гандлярок", raw=true)
                        )),

                Product(alias = "nexgo",
                        name = "Smart POS Nexgo N5 с ПО GTPOS", comment = "iKassa Smart&Card NexGo",
                        price = BigDecimal(825), tax = BigDecimal(20),
                        dualDocs=true,
                        accompanyingDocs = mutableListOf(
                                AccompanyingDoc(PATH_TO_NEXGO, "05 Информация для заключения договора на оборудование")
                        )
                ),

                Product(alias = "pax930_lancard",
                        name = "POS-терминал PAX A930", comment = "iKassa Smart&Card",
                        price = BigDecimal(715), tax = BigDecimal(20),
                        dualDocs=true,
                        //accompanyingDocs = listOf(AccompanyingDoc(PATH_TO_PAX930_LANCARD, "05 Договор на оборудование РАХ А930"))
                        accompanyingDocs = mutableListOf(
                                AccompanyingDoc(PATH_TO_PAX930, "05 Счёт на оборудование РАХ А930"),
                                AccompanyingDoc("docs/static/pax930_instalment_3.docx", "05_1 Рассрочка на оборудование РАХ А930 (3 мес)"),
                                AccompanyingDoc("docs/static/pax930_instalment_6.docx", "05_1 Рассрочка на оборудование РАХ А930 (6 мес)")
                        )
                ),

                Product(alias = "dusik_r",
                        name = "iKassa multi Dusik_r", comment = "iKassa multi Dusik_r",
                        price = BigDecimal(855), tax = BigDecimal(20),
                        accompanyingDocs = mutableListOf()
                ),

                Product(alias = "azur8223_belvti",
                        name = "Бесконтактный POS-терминал AZUR POS, модель KS 8223, с ПО", comment = "iKassa Smart&Card Azur",
                        price = BigDecimal(735.50)/*.toFixed(2)*/, tax = BigDecimal(20), roundTotal = true,
                        dualDocs=true,
                        accompanyingDocs = mutableListOf(AccompanyingDoc(PATH_TO_AZUR8223_BELVTI, "05 Договор на оборудование AZUR POS 8223"))),

                Product(alias = "azur_fm_belvti",
                        name = "Бесконтактный POS-терминал AZUR POS, модель KS 8223, с ПО", comment = "iKassa Smart&Card Azur",
                        price = BigDecimal(332.50)/*.toFixed(2)*/, tax = BigDecimal(20),
                        dualDocs=true,
                        accompanyingDocs = mutableListOf(AccompanyingDoc(PATH_TO_AZUR_FM_BELVTI, "05 Договор на оборудование AZUR POS 8223SK"))),

                Product(alias = "pax930_promo",
                        name = "POS-терминал PAX A930", comment = "iKassa Smart&Card",
                        price = BigDecimal(715), tax = BigDecimal(20),
                        dualDocs=true,
                        accompanyingDocs = mutableListOf(AccompanyingDoc(PATH_TO_PAX930, "05 Счёт на оборудование РАХ А930"))),

                Product(alias = "feitian_f20",
                        name = "POS-терминал Feitian F20", comment = "iKassa Smart&Card",
                        price = BigDecimal(650), tax = BigDecimal(20),
                        dualDocs=true,
                        accompanyingDocs = mutableListOf()),

                Product(alias = "ikassa_license_12_season",
                        name = "Абонентское обслуживание iKassa",
                        comment = "Регистрация Программной кассы (ПК) в АИС ПКС iKassa и Абонентская плата (за 12 месяцев) по тарифу “Сезон Smart”",
                        price = BigDecimal(129.60)/*.toFixed(2)*/),//BigDecimal(14.5)),
                Product(
                        alias = "nexgo_n86",
                        name = "Smart POS Nexgo N86",
                        comment = "iKassa Smart&Card NexGo N86",
                        dualDocs = true,
                        accompanyingDocs = mutableListOf(
                                AccompanyingDoc(PATH_TO_NEXGO, "05 Информация для заключения договора на оборудование")
                        ),
                        price = BigDecimal(747.5),
                        tax = BigDecimal(20)
                )
            )
        )
    }
}