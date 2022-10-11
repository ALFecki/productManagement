package management.services

import jakarta.inject.Singleton
import management.entities.Product
import management.entities.Solution
import management.repositories.SolutionRepository
import javax.transaction.Transactional


@Singleton
class SolutionService (private val solutionRepository: SolutionRepository,
                       private val productService: ProductService) {

    private val defaultProducts : MutableList<Product> = mutableListOf()

    fun getAllSolutions() : MutableList<Solution> {
        return solutionRepository.findAll()
    }

    fun getSolutionByAlias(alias : String) : Solution {
        return solutionRepository.findByAlias(alias)
    }


    fun exportDefaultSolutions() : List<Solution> {
        defaultProducts += productService.getProductByAlias(alias = "ikassa_register")
        defaultProducts += productService.getProductByAlias(alias ="ikassa_license")
        defaultProducts += productService.getProductByAlias(alias ="skko_register")
        defaultProducts += productService.getProductByAlias(alias ="skko_license_6")
        defaultProducts += productService.getProductByAlias(alias ="personal")
        defaultProducts += productService.getProductByAlias(alias ="app")
        return solutionRepository.saveAll(
                listOf(
                        Solution(
                                alias = "smart_and_card_nexgo_n86",
                                name = "iKassa Smart&Card NexGo N86",
                                legalName = "iKassa Smart&Card",
                                contents = defaultProducts,
                                equipment = productService.getProductByAlias(alias = "nexgo_n86")
                        ),
                        Solution(
                            alias = "smart",
                            name = "iKassa Smart",
                            legalName = "iKassa Smart",
                                contents = defaultProducts,
                                related = productService.getProductByAlias(alias = "personal") +
                                        productService.getProductByAlias(alias = "app") +
                                        productService.getProductByAlias(alias = "fm_paymob") +
                                        productService.getProductByAlias(alias = "pax930") +
                                        productService.getProductByAlias(alias = "pax910") +
                                        productService.getProductByAlias(alias = "fm_azur"),
                                equipment = mutableListOf(/*"rpp02n"*/ /*, "gandlarok_mpos"*/),
                                version = "2.5.0"
                        ),
                        Solution(
                            alias = "fm",
                            name = "iKassa FM",
                            legalName = "iKassa Smart",
                            contents = defaultProducts,
                                equipment = productService.getProductByAlias(alias = "fm_paymob") /*, "gandlarok_mpos"*/,
                                version = "2.5.0"
                        ),
                        /*
                         * FIXME: кирилл сейчас юзает smart_and_card_promo а не просто smart_and_card,
                         *   потому цену надо менять и там.
                         *   Кроме того, есть акционная цена на пакс при >=6 месяцев,
                         *   она берется из товара pax930_promo и подставляется в DocumentsController
                         *   для всех паксов.
                         */
                        Solution(
                            alias = "smart_and_card",
                            name = "iKassa Smart&Card PAX A930",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                                related = productService.getProductByAlias(alias = "adapter_typec"),
                                equipment = productService.getProductByAlias(alias = "pax930")
//                                TODO("extraVars = mapOf(\"PROCESSINGPROVIDER\" to \"ОАО «Банк БелВЭБ»\"")
                        ),
                        Solution(
                            alias = "smart_and_card_azur",
                            name = "iKassa Smart&Card Azur",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            related = productService.getProductByAlias(alias = "adapter_typec"),
                            equipment = productService.getProductByAlias(alias = "azur8223")
                        ),
//
//                        Solution(id = "smart_and_card_nexgo", name = "iKassa Smart&Card NexGo", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf("nexgo")
//                        ),
//
//                        Solution(id = "smart_and_card_feitian", name = "iKassa Smart&Card Feitian F20", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf("feitian_f20")
//                        ),
//
//                        Solution(id = "fm_azur", name = "iKassa FM Azur", legalName = "iKassa Smart",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf("fm_azur"),
//                                version = "2.5.0"
//                        ),
//
//                        Solution(id = "dusik_r", name = "iKassa multi Dusik_r", legalName = "iKassa multi Dusik_r",
//                                contents = productsRepository.filter {
//                                    (ikassaBaseList+"dusik_r").contains(it.alias)
//                                },
//                                version = "1.7.1"
//                        ),
//                        /** в dusik_r_partner не входит инвойс на лицензию, только на подключение */
//                        Solution(id = "dusik_r_partner", name = "iKassa multi Dusik_r", legalName = "iKassa multi Dusik_r",
//                                contents = productsRepository.filter {
//                                    listOf(
//                                            "ikassa_register",
//                                            "skko_register",
//                                            "skko_license_6",
//                                            "personal",
//                                            "app"
//                                    ).contains(it.alias)
//                                },
//                                version = "1.7.1"
//                        ),
//
//                        /** в dusik_r_partner_partner это как партнёрский дусик, только однозначно совсем партнёрский дусик.
//                         * в нём нет счёта ни на тариф, ни на подключение
//                         * */
//                        Solution(id = "dusik_r_partner_partner", name = "iKassa multi Dusik_r", legalName = "iKassa multi Dusik_r",
//                                contents = productsRepository.filter {
//                                    listOf(
//                                            "skko_register",
//                                            "skko_license_6",
//                                            "personal",
//                                            "app"
//                                    ).contains(it.alias)
//                                },
//                                version = "1.7.1"
//                        ),
//
//                        /** индивидуальные солюшены для насвязи и а1,
//                         * в них не должно быть документов
//                         * на оборудование
//                         * */
//                        Solution(id = "smart_docless", name = "iKassa Smart", legalName = "iKassa Smart",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf(),
//                                version = "2.5.0"
//                        ),
//                        Solution(id = "fm_docless", name = "iKassa FM", legalName = "iKassa Smart",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf(),
//                                version = "2.5.0"
//                        ),
//                        Solution(id = "smart_and_card_docless", name = "iKassa Smart&Card PAX A930", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf()
//                        ),
//                        Solution(id = "smart_and_card_generic_docless", name = "iKassa Smart&Card", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf()
//                        ),
//                        Solution(id = "smart_and_card_azur_docless", name = "iKassa Smart&Card Azur", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf()
//                        ),
//                        Solution(id = "smart_and_card_nexgo_docless", name = "iKassa Smart&Card NexGo", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf()
//                        ),
//                        Solution(id = "smart_and_card_a910_docless", name = "iKassa Smart&Card PAX A910", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                //equipment = listOf("pax910"),
//                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Приорбанк»")
//                        ),
//                        //ОАО «Банк БелВЭБ» ОАО «Белгазпромбанк»  ОАО «Белинвестбанк»
//                        Solution(id = "smart_and_card_belweb", name = "iKassa Smart&Card ОАО «Банк БелВЭБ»", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf("pax930_lancard"),
//                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Банк БелВЭБ»")
//                        ),
//                        Solution(id = "smart_and_card_belgazprom", name = "iKassa Smart&Card ОАО «Белгазпромбанк»", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf("pax930_lancard"),
//                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Белгазпромбанк»")
//                        ),
//                        Solution(id = "smart_and_card_belinvest", name = "iKassa Smart&Card ОАО «Белинвестбанк»", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf("pax930_lancard"),
//                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Белинвестбанк»")
//                        ),
//                        Solution(id = "smart_and_card_belarusbank", name = "iKassa Smart&Card ОАО «АСБ Беларусбанк»", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf("pax930_lancard"),
//                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «АСБ Беларусбанк»")
//                        ),
//                        Solution(id = "smart_and_card_rrbbank", name = "iKassa Smart&Card ЗАО «РРБ-Банк»", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf("pax930_lancard"),
//                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Банк БелВЭБ»")
//                        ),
//                        Solution(id = "smart_and_card_paritetbank", name = "iKassa Smart&Card ОАО «Паритетбанк»", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf("pax930_lancard"),
//                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Паритетбанк»"),
//                                forcedInstructionPdf = AccompanyingDoc(path="static/forced_paritet.pdf", name="00-Инструкция.pdf", raw=true)
//                        ),
//                        Solution(id = "smart_and_card_belagroprombank", name = "iKassa Smart&Card ОАО «Белагропромбанк»", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf("pax930_lancard"),
//                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Белагропромбанк»")
//                        ),
//
//                        Solution(id = "smart_and_card_azur_belweb", name = "iKassa Smart&Card Azur ОАО «Банк БелВЭБ»", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf("azur8223_belvti"),
//                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Банк БелВЭБ»")
//                        ),
//                        Solution(id = "smart_and_card_azur_prior", name = "iKassa Smart&Card Azur ОАО «Приорбанк»", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf("azur8223_belvti"),
//                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Приорбанк»")
//                        ),
//                        Solution(id = "smart_and_card_azur_fm", name = "iKassa Smart&Card 2в1", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf("azur_fm_belvti"),
//                                //extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Банк БелВЭБ»")
//                        ),
//                        Solution(id = "smart_a1", name = "iKassa Smart", legalName = "iKassa Smart",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                //equipment = listOf(/*"rpp02n", */"adapter_typec", "adapter_microusb", "adapter_microusb_whippy" /*, "gandlarok_mpos"*/),
//                                forcedInstructionPdf = AccompanyingDoc(path="static/forced_a1_smart.pdf", name="00-Инструкция.pdf", raw=true),
//                                version = "2.5.0"
//                        ),
//                        Solution(id = "smart_and_card_docless_a1", name = "iKassa Smart&Card PAX A930", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf(),
//                                forcedInstructionPdf = AccompanyingDoc(path="static/forced_a1_pax.pdf", name="00-Инструкция.pdf", raw=true)
//                        ),
//                        Solution(id = "smart_and_card_a910", name = "iKassa Smart&Card PAX A910", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf("pax910"),
//                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Приорбанк»")
//                        ),
//                        Solution(id = "smart_and_card_promo", name = "iKassa Smart&Card PAX A930", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                related = productsRepository.filter {
//                                    listOf<Short>(13).contains(it.id)
//                                },
//                                equipment = listOf("pax930"),
//                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Банк БелВЭБ»")
//                        ),
//                        Solution(id = "smart_and_card_tehnobank", name = "iKassa Smart&Card PAX A930", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                related = productsRepository.filter {
//                                    listOf<Short>(13).contains(it.id)
//                                },
//                                //equipment = listOf("pax930"),
//                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Технобанк»")
//                        )
//                        /**
//                         * TODO: легаси-мусор чтоб у НаСвязи не поотваливались формы сразу после обновления.
//                         *   удалить на следующем релизе
//                         **/
//                        ,
//                        Solution(id = "smart_nsv", name = "iKassa Smart", legalName = "iKassa Smart",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf(),
//                                version = "2.5.0"
//                        ),
//                        Solution(id = "smart_and_card_nsv", name = "iKassa Smart&Card PAX A930", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf()
//                        ),
//                        Solution(id = "smart_and_card_nexgo_nsv", name = "iKassa Smart&Card NexGo", legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    ikassaBaseList.contains(it.alias)
//                                },
//                                equipment = listOf()
//                        ),
//
//                        /** Солюшен-заглушка, который нельзя выбрать */
//                        Solution(id = "stub", name = "= Выберите решение =", legalName = "",
//                                contents = emptyList(),
//                                related = emptyList(),
//                                equipment = emptyList()
//                        ),
//                        Solution(id = "stub_a1", name = "", legalName = "",
//                                contents = emptyList(),
//                                related = emptyList(),
//                                equipment = emptyList()
//                        ),
//
//                        /**
//                         * Сезонные тарифы
//                         * */
//                        Solution(
//                                id = "smart_season",
//                                name = "iKassa Smart на смартфоне",
//                                legalName = "iKassa Smart",
//                                contents = productsRepository.filter {
//                                    listOf(
//                                            "ikassa_license_12_season",
//                                            "skko_register",
//                                            "skko_license_6",
//                                            "personal",
//                                            "app"
//                                    ).contains(it.alias)
//                                },
//                                equipment = listOf("smart_docless"),
//                                version = "2.5.0"
//                        ),
//                        Solution(
//                                id = "azur_fm_season",
//                                name = "iKassa Smart 2в1 на Azur POS SK",
//                                legalName = "iKassa Smart",
//                                contents = productsRepository.filter {
//                                    listOf(
//                                            "ikassa_license_12_season",
//                                            "skko_register",
//                                            "skko_license_6",
//                                            "personal",
//                                            "app"
//                                    ).contains(it.alias)
//                                },
//                                equipment = listOf("azur_fm_belvti"),
//                                version = "2.5.0"
//                        ),
//                        Solution(
//                                id = "feitian_f20_season",
//                                name = "iKassa Smart&Card на Feitian F20",
//                                legalName = "iKassa Smart&Card",
//                                contents = productsRepository.filter {
//                                    listOf(
//                                            "ikassa_license_12_season",
//                                            "skko_register",
//                                            "skko_license_6",
//                                            "personal",
//                                            "app"
//                                    ).contains(it.alias)
//                                },
//                                equipment = listOf("feitian_f20"),
//                        ),
                )
        )
    }


}