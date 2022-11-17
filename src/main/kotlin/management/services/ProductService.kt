package management.services

import jakarta.inject.Singleton
import management.data.products.AccompanyingDoc
import management.data.products.Product
import management.data.repositories.AccompanyingDocRepository
import management.data.repositories.ProductRepository
import management.data.utils.UpdateProduct
import management.forms.AccompanyingDocDto
import management.forms.ProductDto
import management.utils.FilePath.PATH_TO_ADAPTER_MICROUSB
import management.utils.FilePath.PATH_TO_ADAPTER_MICROUSB_WHIPPY
import management.utils.FilePath.PATH_TO_ADAPTER_TYPEC
import management.utils.FilePath.PATH_TO_AZUR8223
import management.utils.FilePath.PATH_TO_AZUR8223_BELVTI
import management.utils.FilePath.PATH_TO_AZUR_FM
import management.utils.FilePath.PATH_TO_AZUR_FM_BELVTI
import management.utils.FilePath.PATH_TO_FM
import management.utils.FilePath.PATH_TO_NEXGO
import management.utils.FilePath.PATH_TO_PAX910
import management.utils.FilePath.PATH_TO_PAX930
import management.utils.FilePath.PATH_TO_PAX930_BAG
import management.utils.FilePath.PATH_TO_PRINTER_RPP02A
import management.utils.FilePath.PATH_TO_PRINTER_RPP02N
import management.utils.toFixed
import java.math.BigDecimal


@Singleton
class ProductService(
    private val productRepository: ProductRepository,
    private val accompanyingDocRepository: AccompanyingDocRepository
) {

    fun makeProducts(products: List<ProductDto>): MutableList<Product> {
        val productList: MutableList<Product> = mutableListOf()

        if (products.isEmpty())
            throw IllegalStateException("Not enough data in request")

        products.forEach { product ->
            productList.add(
                productRepository.findByAlias(product.alias)
                    ?: makeProduct(product)
            )
        }
        return productList
    }

    private fun makeProduct(product: ProductDto): Product {
        return Product(
            alias = product.alias,
            name = product.name,
            comment = product.comment ?: "",
            price = product.price, // "price":123.4
            tax = product.tax ?: BigDecimal.ZERO,
            currency = product.currency ?: "",
            units = product.units ?: "", // FIXME
            roundTotal = product.roundTotal ?: false, // "round_total":true
            dualDocs = product.dualDocs ?: false, // "dual_docs":true
            accompanyingDocs = makeAccompanyingDocs(product.accompanyingDocs ?: listOf())
        )
    }

    fun makeAccompanyingDoc(doc: AccompanyingDocDto): AccompanyingDoc {
        return accompanyingDocRepository.findByPath(doc.path)
            ?: AccompanyingDoc(
                path = doc.path,
                name = doc.name,
                raw = doc.raw
            )
    }

    fun makeAccompanyingDocs(docs: List<AccompanyingDocDto>): List<AccompanyingDoc> {
        val accompanyingDocList: MutableList<AccompanyingDoc> = mutableListOf()
        if (docs.isEmpty()) return accompanyingDocList
        docs.forEach { doc ->
            accompanyingDocList.add(
                accompanyingDocRepository.findByPath(doc.path)
                    ?: this.makeAccompanyingDoc(doc)
            )
        }
        return accompanyingDocList
    }

    fun getAllProducts(): MutableList<Product> {
        return productRepository.findAll()
    }

    fun getProductByAlias(alias: String): Product? {
        return productRepository.findByAlias(alias)
    }

    fun createProduct(productData: ProductDto): Product {
        return productRepository.save(makeProduct(productData))
    }

    fun createProducts(products: List<ProductDto>): MutableList<Product> {
        return productRepository.saveAll(makeProducts(products))
    }

    fun updateProductName(alias: String, requestData: UpdateProduct): Product {
        val product = productRepository.findByAlias(alias)
            ?: throw IllegalStateException("Cannot find product with alias $alias")
        product.name = requestData.name
            ?: throw IllegalStateException("No data in request")
        return productRepository.update(product)
    }

    fun updateProductComment(alias: String, requestData: UpdateProduct): Product {
        val product = productRepository.findByAlias(alias)
            ?: throw IllegalStateException("Cannot find product with alias $alias")
        product.comment = requestData.comment
            ?: throw IllegalStateException("No data in request")
        return productRepository.update(product)
    }


    fun updateProductPrice(alias: String, requestData: UpdateProduct): Product {
        val product = productRepository.findByAlias(alias)
            ?: throw IllegalStateException("Cannot find product with alias $alias")
        product.price = requestData.price
            ?: throw IllegalStateException("No data in request")
        return productRepository.update(product)
    }

    fun updateProductTax(alias: String, requestData: UpdateProduct): Product {
        val product = productRepository.findByAlias(alias)
            ?: throw IllegalStateException("Cannot find product with alias $alias")
        product.tax = requestData.tax
            ?: throw IllegalStateException("No data in request")
        return productRepository.update(product)
    }

    fun updateProductDualDocs(alias: String, requestData: UpdateProduct): Product {
        val product = productRepository.findByAlias(alias)
            ?: throw IllegalStateException("Cannot find product with alias $alias")
        product.dualDocs = requestData.dualDocs
            ?: throw IllegalStateException("No data in request")
        return productRepository.update(product)
    }

    fun updateProductRoundTotal(alias: String, requestData: UpdateProduct) : Product {
        val product = productRepository.findByAlias(alias)
            ?: throw IllegalStateException("Cannot find product with alias $alias")
        product.dualDocs = requestData.roundTotal
            ?: throw IllegalStateException("No data in request")
        return productRepository.update(product)
    }
    fun updateProductDocs(alias: String, docs: List<AccompanyingDocDto>): Product? {
        val product = productRepository.findByAlias(alias) ?: throw (Exception("No such product in database"))
        product.accompanyingDocs = makeAccompanyingDocs(docs)
        return productRepository.update(product)
    }

    fun addProductDocs(alias: String, docs: List<AccompanyingDocDto>): Product? {
        val product = productRepository.findByAlias(alias) ?: throw (Exception("No such product in database"))
        product.accompanyingDocs += makeAccompanyingDocs(docs)
        return productRepository.update(product)
    }

    fun deleteProduct(alias: String) {
        return productRepository.deleteByAlias(alias)
    }


    fun exportDefault(): List<Product> {
        return productRepository.saveAll(
            listOf(

                Product(
                    alias = "ikassa_register",
                    name = "Регистрация программной кассы (включая СКО)",
                    comment = "Регистрация Программной кассы (ПК) в АИС ПКС iKassa",
                    price = BigDecimal(53)
                ),
                Product(
                    alias = "ikassa_register_only",
                    name = "Регистрация программной кассы (включая СКО)",
                    comment = "Регистрация Программной кассы (ПК) в АИС ПКС iKassa",
                    price = BigDecimal(53)
                ),
                Product(
                    alias = "ikassa_license",
                    name = "Абонентское обслуживание iKassa",
                    comment = "Абонентская плата (за 1 месяц)",
                    price = BigDecimal(15)
                ),
                Product(
                    alias = "ikassa_license_3", //ikassa license 3
                    name = "Абонентское обслуживание iKassa",
                    comment = "Абонентская плата (за 3 месяца)",
                    price = BigDecimal(45)
                ),
                Product(
                    alias = "ikassa_license_6",
                    name = "Абонентское обслуживание iKassa",
                    comment = "Абонентская плата (за 6 месяцев)",
                    price = BigDecimal(87)
                ),//BigDecimal(14.5)),
                Product(
                    alias = "ikassa_license_12",
                    name = "Абонентское обслуживание iKassa",
                    comment = "Абонентская плата (за 12 месяцев)",
                    price = BigDecimal(174)
                ),//BigDecimal(14.5)),
                Product(
                    alias = "ikassa_license_dusik",
                    name = "Абонентское обслуживание iKassa",
                    comment = "Абонентская плата (за 1 месяц)",
                    price = BigDecimal(20)
                ),
                Product(
                    alias = "ikassa_license_3_dusik", //ikassa license 3
                    name = "Абонентское обслуживание iKassa",
                    comment = "Абонентская плата (за 3 месяца)",
                    price = BigDecimal(60)
                ),
                Product(
                    alias = "ikassa_license_6_dusik",
                    name = "Абонентское обслуживание iKassa",
                    comment = "Абонентская плата (за 6 месяцев)",
                    price = BigDecimal(120)
                ),//BigDecimal(14.5)),
                Product(
                    alias = "ikassa_license_12_dusik",
                    name = "Абонентское обслуживание iKassa",
                    comment = "Абонентская плата (за 12 месяцев)",
                    price = BigDecimal(240)
                ),//BigDecimal(14.5)),
                Product(
                    alias = "skko_register",
                    name = "Регистрация программной кассы в СККО",
                    comment = "",
                    price = BigDecimal(2.50),
                    tax = BigDecimal(20)
                ),
                Product(
                    alias = "skko_license_6",
                    name = "Оплата за обслуживание программной кассы в СККО",
                    comment = "Информационное обслуживание пользователя программной кассы за 6 месяцев",
                    price = BigDecimal(25),
                    tax = BigDecimal(20)
                ),
                Product(
                    alias = "skko_license_12",
                    name = "Оплата за обслуживание программной кассы в СККО",
                    comment = "Информационное обслуживание пользователя программной кассы за 12 месяцев",
                    price = BigDecimal(50),
                    tax = BigDecimal(20)
                ),

                Product(
                    alias = "personal",
                    name = "Личный Кабинет",
                    price = BigDecimal.ZERO
                ),

                Product(
                    alias = "app",
                    name = "Программная Касса",
                    comment = "само приложение",
                    price = BigDecimal.ZERO
                ),

                Product(
                    alias = "fm_paymob",
                    name = "Мобильный компьютер PayMob-M1",
                    comment = "iKassa FM",
                    price = BigDecimal(315),
                    tax = BigDecimal(20),
                    accompanyingDocs = listOf(
                        AccompanyingDoc(
                            PATH_TO_FM,
                            "05 Реквизиты для оплаты устройства PayMob-M1"
                        )
                    )
                ),

                Product(
                    alias = "pax930",
                    name = "POS-терминал PAX A930",
                    comment = "iKassa Smart&Card",
                    price = BigDecimal(715),
                    tax = BigDecimal(20),
                    dualDocs = true,
                    accompanyingDocs = listOf(
                        AccompanyingDoc(PATH_TO_PAX930, "05 Счёт на оборудование РАХ А930"),
                        AccompanyingDoc(
                            "docs/static/pax930_instalment_3.docx",
                            "05_3 Рассрочка на оборудование РАХ А930 (3 мес)"
                        ),
                        AccompanyingDoc(
                            "docs/static/pax930_instalment_6.docx",
                            "05_6 Рассрочка на оборудование РАХ А930 (6 мес)"
                        )
                    )
                ),
                Product(
                    alias = "pax910",
                    name = "POS-терминал PAX A910",
                    comment = "iKassa Smart&Card",
                    price = BigDecimal(791.67).toFixed(2),
                    tax = BigDecimal(20),
                    dualDocs = true,
                    accompanyingDocs = listOf(AccompanyingDoc(PATH_TO_PAX910, "05 Договор на оборудование РАХ А910"))
                ),

                Product(
                    alias = "fm_azur",
                    name = "Кассовый аппарат «AZUR POS», модель KS8223SK",
                    comment = "iKassa FM Azur",
                    price = BigDecimal(424.16).toFixed(2),
                    tax = BigDecimal(20),
                    roundTotal = true,
                    dualDocs = true,
                    accompanyingDocs = listOf(
                        AccompanyingDoc(
                            PATH_TO_AZUR_FM,
                            "05 Реквизиты для оплаты устройства FM Azur"
                        )
                    )
                ),

                Product(
                    alias = "adapter_typec", name = "Адаптер USB-USB Type-C",
                    price = BigDecimal(7.0),
                    accompanyingDocs = listOf(AccompanyingDoc(PATH_TO_ADAPTER_TYPEC, name = "Адаптер USB-USB Type-C"))
                ),

                Product(
                    alias = "adapter_microusb", name = "Адаптер USB-MicroUSB",
                    price = BigDecimal(7.0),
                    accompanyingDocs = listOf(AccompanyingDoc(PATH_TO_ADAPTER_MICROUSB, name = "Адаптер USB-MicroUSB"))
                ),

                Product(
                    alias = "adapter_microusb_whippy", name = "Адаптер гибкий USB-MicroUSB",
                    price = BigDecimal(8.0),
                    accompanyingDocs = listOf(
                        AccompanyingDoc(
                            PATH_TO_ADAPTER_MICROUSB_WHIPPY,
                            name = "Адаптер гибкий USB-MicroUSB"
                        )
                    )
                ),

                Product(
                    alias = "rpp02n", name = "Мобильный принтер RPP02N", comment = "Чёрный принтер",
                    price = BigDecimal(100), tax = BigDecimal(20),
                    dualDocs = true,
                    accompanyingDocs = listOf(
                        AccompanyingDoc(
                            PATH_TO_PRINTER_RPP02N,
                            "05 Реквизиты для оплаты мобильного принтера"
                        )
                    )
                ),

                Product(
                    alias = "rpp02a", name = "Мобильный принтер RPP02A", comment = "С рыжими кнопками",
                    price = BigDecimal(100), tax = BigDecimal(20),
                    dualDocs = true,
                    accompanyingDocs = listOf(
                        AccompanyingDoc(
                            PATH_TO_PRINTER_RPP02A,
                            "05 Реквизиты для оплаты мобильного принтера"
                        )
                    )
                ),

                Product(
                    alias = "bag", name = "Сумка-чехол с ремешком", comment = "Для PAX 930 (smart&card)",
                    price = BigDecimal(55),
                    accompanyingDocs = listOf(AccompanyingDoc(PATH_TO_PAX930_BAG, "Сумка-чехол"))
                ),

                Product(
                    alias = "azur8223",
                    name = "Бесконтактный POS-терминал AZUR POS, модель KS 8223, с ПО",
                    comment = "iKassa Smart&Card Azur",
                    price = BigDecimal(958.33).toFixed(2),
                    tax = BigDecimal(20),
                    roundTotal = true,
                    dualDocs = true,
                    accompanyingDocs = listOf(
                        AccompanyingDoc(
                            PATH_TO_AZUR8223,
                            "05 Договор на оборудование AZUR POS 8223"
                        )
                    )
                ),

                Product(
                    alias = "gandlarok_mpos",
                    name = "Считыватель Vi 218 с программным протоколом взаимодействия с программным обеспечением «ГандлярОК» (магнитная полоса, чип, бесконтакт)",
                    comment = "Гандлярок MPOS",
                    price = BigDecimal(250), tax = BigDecimal(20),
                    accompanyingDocs = listOf(
                        AccompanyingDoc("docs/products/gandlarok_invoice.xlsm", "10 Счёт ГандлярОК", raw = true),
                        AccompanyingDoc(
                            "docs/products/gandlarok_doc.docx",
                            "11 Договор купли-продажи Гандлярок",
                            raw = true
                        )
                    )
                ),

                Product(
                    alias = "nexgo",
                    name = "Smart POS Nexgo N5 с ПО GTPOS", comment = "iKassa Smart&Card NexGo",
                    price = BigDecimal(825), tax = BigDecimal(20),
                    dualDocs = true,
                    accompanyingDocs = listOf(
                        AccompanyingDoc(PATH_TO_NEXGO, "05 Информация для заключения договора на оборудование")
                    )
                ),

                Product(
                    alias = "pax930_lancard",
                    name = "POS-терминал PAX A930", comment = "iKassa Smart&Card",
                    price = BigDecimal(715), tax = BigDecimal(20),
                    dualDocs = true,
                    //accompanyingDocs = listOf(AccompanyingDoc(PATH_TO_PAX930_LANCARD, "05 Договор на оборудование РАХ А930"))
                    accompanyingDocs = listOf(
                        AccompanyingDoc(PATH_TO_PAX930, "05 Счёт на оборудование РАХ А930"),
                        AccompanyingDoc(
                            "docs/static/pax930_instalment_3.docx",
                            "05_1 Рассрочка на оборудование РАХ А930 (3 мес)"
                        ),
                        AccompanyingDoc(
                            "docs/static/pax930_instalment_6.docx",
                            "05_1 Рассрочка на оборудование РАХ А930 (6 мес)"
                        )
                    )
                ),

                Product(
                    alias = "dusik_r",
                    name = "iKassa multi Dusik_r", comment = "iKassa multi Dusik_r",
                    price = BigDecimal(855), tax = BigDecimal(20),
                    accompanyingDocs = listOf()
                ),

                Product(
                    alias = "azur8223_belvti",
                    name = "Бесконтактный POS-терминал AZUR POS, модель KS 8223, с ПО",
                    comment = "iKassa Smart&Card Azur",
                    price = BigDecimal(735.50).toFixed(2),
                    tax = BigDecimal(20),
                    roundTotal = true,
                    dualDocs = true,
                    accompanyingDocs = listOf(
                        AccompanyingDoc(
                            PATH_TO_AZUR8223_BELVTI,
                            "05 Договор на оборудование AZUR POS 8223"
                        )
                    )
                ),

                Product(
                    alias = "azur_fm_belvti",
                    name = "Бесконтактный POS-терминал AZUR POS, модель KS 8223, с ПО",
                    comment = "iKassa Smart&Card Azur",
                    price = BigDecimal(332.50).toFixed(2),
                    tax = BigDecimal(20),
                    dualDocs = true,
                    accompanyingDocs = listOf(
                        AccompanyingDoc(
                            PATH_TO_AZUR_FM_BELVTI,
                            "05 Договор на оборудование AZUR POS 8223SK"
                        )
                    )
                ),

                Product(
                    alias = "pax930_promo",
                    name = "POS-терминал PAX A930", comment = "iKassa Smart&Card",
                    price = BigDecimal(715), tax = BigDecimal(20),
                    dualDocs = true,
                    accompanyingDocs = listOf(AccompanyingDoc(PATH_TO_PAX930, "05 Счёт на оборудование РАХ А930"))
                ),

                Product(
                    alias = "feitian_f20",
                    name = "POS-терминал Feitian F20", comment = "iKassa Smart&Card",
                    price = BigDecimal(650), tax = BigDecimal(20),
                    dualDocs = true,
                    accompanyingDocs = listOf()
                ),

                Product(
                    alias = "ikassa_license_12_season",
                    name = "Абонентское обслуживание iKassa",
                    comment = "Регистрация Программной кассы (ПК) в АИС ПКС iKassa и Абонентская плата (за 12 месяцев) по тарифу “Сезон Smart”",
                    price = BigDecimal(129.60).toFixed(2)
                ),//BigDecimal(14.5)),
                Product(
                    alias = "nexgo_n86",
                    name = "Smart POS Nexgo N86",
                    comment = "iKassa Smart&Card NexGo N86",
                    dualDocs = true,
                    accompanyingDocs = listOf(
                        AccompanyingDoc(PATH_TO_NEXGO, "05 Информация для заключения договора на оборудование")
                    ),
                    price = BigDecimal(747.5),
                    tax = BigDecimal(20)
                )
            )
        )
    }
}