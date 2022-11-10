package management.services

import io.micronaut.json.tree.JsonArray
import io.micronaut.json.tree.JsonNode
import jakarta.inject.Singleton
import management.data.products.AccompanyingDoc
import management.data.products.Product
import management.data.products.Solution
import management.data.repositories.SolutionRepository
import management.forms.AccompanyingDocDto
import management.forms.ProductDto
import management.forms.SolutionDto


@Singleton
class SolutionService (private val solutionRepository: SolutionRepository,
                       private val productService: ProductService,
//                       private val fillDocumentService: FillDocumentService,
//                       private val partnerService: PartnerService
    ) {

    private var defaultProducts : MutableList<Product> = mutableListOf()

    fun makeSolution(solution : SolutionDto) : Solution {
        return Solution(
            alias = solution.alias,
            name = solution.name,
            contents = productService.makeProducts(solution.contents),
            related = productService.makeProducts(solution.related),
            price = solution.price,
            accompanyingDoc = productService.makeAccompanyingDocs(solution.accompanyingDoc),
            equipment = productService.makeProducts(solution.equipment),
            extraVars = solution.extraVars,
            legalName = solution.legalName,
            version = solution.version,
            forcedInstructionPdf = productService.makeAccompanyingDoc(solution.instruction)
        )
    }

    fun makeSolutions(solutions : List<SolutionDto>) : MutableList<Solution> {
        val newSolutions : MutableList<Solution> = mutableListOf()
        if (solutions.isEmpty())
            throw IllegalStateException("Not enough data in request")
        solutions.forEach { solution ->
            newSolutions.add(
                solutionRepository.findByAlias(solution.alias)
                    ?: this.makeSolution(solution)
            )
        }
        return newSolutions
    }

    fun getAllSolutions() : MutableList<Solution> {
        return solutionRepository.findAll()
    }

    fun getSolutionByAlias(alias : String) : Solution? {
        return solutionRepository.findByAlias(alias)
    }

    fun createSolution(solution: SolutionDto) : Solution {
        return solutionRepository.save(makeSolution(solution))
    }

    fun createSolutions(solutions: List<SolutionDto>) : MutableList<Solution> {
        return solutionRepository.saveAll(makeSolutions(solutions))
    }

    fun updateSolutionName(alias : String, name : Map<String, String>) {
        return solutionRepository.updateByAlias(alias, name["name"]!!)
    }

    fun updateSolutionPrice(alias : String, price : Map<String, String>) {
        return solutionRepository.updateByAlias(alias, price["price"]!!.toBigDecimal())
    }

    fun updateSolutionLegalName(alias : String, legalName : Map<String, String>) : Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.legalName = legalName["legal_name"]!!
        return solutionRepository.update(solution)
    }

    fun updateSolutionVersion(alias: String, version : Map<String, String>) : Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.legalName = version["version"]!!
        return solutionRepository.update(solution)
    }

    fun updateSolutionContent(alias : String, content : List<ProductDto>) : Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.contents = productService.makeProducts(content)
        return solutionRepository.update(solution)
    }

    fun updateSolutionEquipment(alias: String, equipment : List<ProductDto>) : Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.equipment = productService.makeProducts(equipment)
        return solutionRepository.update(solution)
    }

    fun updateSolutionRelated(alias : String, related : List<ProductDto>) : Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.related = productService.makeProducts(related)
        return solutionRepository.update(solution)
    }

    fun updateSolutionInstruction(alias : String, instruction : AccompanyingDocDto) : Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.forcedInstructionPdf = productService.makeAccompanyingDoc(instruction)
        return solutionRepository.update(solution)
    }

    fun addSolutionContent(alias : String, content : List<ProductDto>) : Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.contents += productService.makeProducts(content)
        return solutionRepository.update(solution)
    }

    fun addSolutionEquipment(alias: String, equipment : List<ProductDto>) : Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.equipment += productService.makeProducts(equipment)
        return solutionRepository.update(solution)
    }

    fun addSolutionRelated(alias : String, related : List<ProductDto>) : Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.related += productService.makeProducts(related)
        return solutionRepository.update(solution)
    }

    fun deleteSolution(alias : String) {
        return solutionRepository.deleteByAlias(alias)
    }

    fun exportDefaultSolutions() : List<Solution> {
//        productService.exportDefault()
//        fillDocumentService.exportDefaultDocs()
//        partnerService.exportDefault()
        defaultProducts.add(productService.getProductByAlias(alias = "ikassa_register")!!)
        defaultProducts.add(productService.getProductByAlias(alias ="ikassa_license")!!)
        defaultProducts.add(productService.getProductByAlias(alias ="skko_register")!!)
        defaultProducts.add(productService.getProductByAlias(alias ="skko_license_6")!!)
        defaultProducts.add(productService.getProductByAlias(alias ="personal")!!)
        defaultProducts.add(productService.getProductByAlias(alias ="app")!!)
        return solutionRepository.saveAll(
                listOf(
                        Solution(
                            alias = "smart_and_card_nexgo_n86",
                            name = "iKassa Smart&Card NexGo N86",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            equipment = listOf(productService.getProductByAlias(alias = "nexgo_n86")!!)
                        ),
                        Solution(
                            alias = "smart",
                            name = "iKassa Smart",
                            legalName = "iKassa Smart",
                                contents = defaultProducts,
                                related = listOf(productService.getProductByAlias(alias = "personal")!!) +
                                        listOf(productService.getProductByAlias(alias = "app")!!) +
                                        listOf(productService.getProductByAlias(alias = "fm_paymob")!!) +
                                        listOf(productService.getProductByAlias(alias = "pax930")!!) +
                                        listOf(productService.getProductByAlias(alias = "pax910")!!) +
                                        listOf(productService.getProductByAlias(alias = "fm_azur")!!),
                                equipment = listOf(/*"rpp02n"*/ /*, "gandlarok_mpos"*/),
                                version = "2.5.0"
                        ),
                        Solution(
                            alias = "fm",
                            name = "iKassa FM",
                            legalName = "iKassa Smart",
                            contents = defaultProducts,
                            equipment = listOf(productService.getProductByAlias(alias = "fm_paymob")!!) /*, "gandlarok_mpos"*/,
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
                                related = listOf(productService.getProductByAlias(alias = "adapter_typec")!!),
                                equipment = listOf(productService.getProductByAlias(alias = "pax930")!!),
                                extraVars = (mapOf("PROCESSINGPROVIDER" to "ОАО «Банк БелВЭБ»"))
                        ),
                        Solution(
                            alias = "smart_and_card_azur",
                            name = "iKassa Smart&Card Azur",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            related = listOf(productService.getProductByAlias(alias = "adapter_typec")!!),
                            equipment = listOf(productService.getProductByAlias(alias = "azur8223")!!)
                        ),
                        Solution(
                            alias = "smart_and_card_nexgo",
                            name = "iKassa Smart&Card NexGo",
                            legalName = "iKassa Smart&Card",
                            contents =defaultProducts,
                            equipment = listOf(productService.getProductByAlias(alias = "nexgo")!!)
                        ),
                        Solution(
                            alias = "smart_and_card_feitian",
                            name = "iKassa Smart&Card Feitian F20",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            equipment = listOf(productService.getProductByAlias(alias ="feitian_f20")!!)
                        ),
                        Solution(
                            alias = "fm_azur",
                            name = "iKassa FM Azur",
                            legalName = "iKassa Smart",
                            contents = defaultProducts,
                            equipment = listOf(productService.getProductByAlias(alias = "fm_azur")!!),
                            version = "2.5.0"
                        ),
                        Solution(
                            alias = "dusik_r",
                            name = "iKassa multi Dusik_r",
                            legalName = "iKassa multi Dusik_r",
                            contents = defaultProducts + listOf(productService.getProductByAlias(alias = "dusik_r")!!),
                            version = "1.7.1"
                        ),
                        /** в dusik_r_partner не входит инвойс на лицензию, только на подключение */
                        Solution(
                            alias = "dusik_r_partner",
                            name = "iKassa multi Dusik_r",
                            legalName = "iKassa multi Dusik_r",
                            contents = listOf(productService.getProductByAlias(alias = "ikassa_register")!!) +
                                    listOf(productService.getProductByAlias(alias = "skko_register")!!) +
                                    listOf(productService.getProductByAlias(alias = "skko_license_6")!!) +
                                    listOf(productService.getProductByAlias(alias = "personal")!!) +
                                    listOf(productService.getProductByAlias(alias = "app")!!) ,
                            version = "1.7.1"
                        ),
                        /** в dusik_r_partner_partner это как партнёрский дусик, только однозначно совсем партнёрский дусик.
                         * в нём нет счёта ни на тариф, ни на подключение
                         * */
                        Solution(
                            alias = "dusik_r_partner_partner",
                            name = "iKassa multi Dusik_r",
                            legalName = "iKassa multi Dusik_r",
                            contents = listOf(productService.getProductByAlias(alias = "skko_register")!!) +
                                    listOf(productService.getProductByAlias(alias = "skko_license_6")!!) +
                                    listOf(productService.getProductByAlias(alias = "personal")!!) +
                                    listOf(productService.getProductByAlias(alias = "app")!!),
                            version = "1.7.1"
                        ),
                        /** индивидуальные солюшены для насвязи и а1,
                         * в них не должно быть документов
                         * на оборудование
                         * */
                        Solution(
                            alias = "smart_docless",
                            name = "iKassa Smart",
                            legalName = "iKassa Smart",
                            contents = defaultProducts,
                            version = "2.5.0"
                        ),
                        Solution(alias = "fm_docless",
                            name = "iKassa FM",
                            legalName = "iKassa Smart",
                            contents = defaultProducts,
                            version = "2.5.0"
                        ),
                        Solution(alias = "smart_and_card_docless",
                            name = "iKassa Smart&Card PAX A930",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts
                        ),
                        Solution(alias = "smart_and_card_generic_docless",
                            name = "iKassa Smart&Card",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts
                        ),
                        Solution(alias = "smart_and_card_azur_docless",
                            name = "iKassa Smart&Card Azur",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts
                        ),
                        Solution(alias = "smart_and_card_nexgo_docless",
                            name = "iKassa Smart&Card NexGo",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts
                        ),
                        Solution(alias = "smart_and_card_a910_docless",
                            name = "iKassa Smart&Card PAX A910",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                                //equipment = listOf("pax910"),

                            extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Приорбанк»")
                        ),
                        //ОАО «Банк БелВЭБ» ОАО «Белгазпромбанк»  ОАО «Белинвестбанк»
                        Solution(alias = "smart_and_card_belweb",
                            name = "iKassa Smart&Card ОАО «Банк БелВЭБ»",
                            legalName = "iKassa Smart&Card",
                                contents = defaultProducts,
                                equipment = listOf(productService.getProductByAlias(alias ="pax930_lancard")!!),
                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Банк БелВЭБ»")
                        ),
                        Solution(alias = "smart_and_card_belgazprom",
                            name = "iKassa Smart&Card ОАО «Белгазпромбанк»",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            equipment = listOf(productService.getProductByAlias(alias ="pax930_lancard")!!),
                                extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Белгазпромбанк»")
                        ),
                        Solution(
                            alias = "smart_and_card_belinvest",
                            name = "iKassa Smart&Card ОАО «Белинвестбанк»",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            equipment = listOf(productService.getProductByAlias(alias ="pax930_lancard")!!),
                            extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Белинвестбанк»")
                        ),
                        Solution(alias = "smart_and_card_belarusbank",
                            name = "iKassa Smart&Card ОАО «АСБ Беларусбанк»",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            equipment = listOf(productService.getProductByAlias(alias ="pax930_lancard")!!),
                            extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «АСБ Беларусбанк»")
                        ),
                        Solution(alias = "smart_and_card_rrbbank",
                            name = "iKassa Smart&Card ЗАО «РРБ-Банк»",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            equipment = listOf(productService.getProductByAlias(alias ="pax930_lancard")!!),
                            extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Банк БелВЭБ»")
                        ),
                        Solution(alias = "smart_and_card_paritetbank",
                            name = "iKassa Smart&Card ОАО «Паритетбанк»",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            equipment = listOf(productService.getProductByAlias(alias ="pax930_lancard")!!),
                            extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Паритетбанк»"),
                            forcedInstructionPdf = AccompanyingDoc(path = "static/forced_paritet.pdf", name = "00-Инструкция.pdf", raw = true)
                        ),
                        Solution(
                            alias = "smart_and_card_belagroprombank",
                            name = "iKassa Smart&Card ОАО «Белагропромбанк»",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            equipment = listOf(productService.getProductByAlias(alias ="pax930_lancard")!!),
                            extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Белагропромбанк»")
                        ),

                        Solution(alias = "smart_and_card_azur_belweb",
                            name = "iKassa Smart&Card Azur ОАО «Банк БелВЭБ»",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            equipment = listOf(productService.getProductByAlias(alias = "azur8223_belvti")!!),
                            extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Банк БелВЭБ»")
                        ),
                        Solution(alias = "smart_and_card_azur_prior",
                            name = "iKassa Smart&Card Azur ОАО «Приорбанк»",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            equipment = listOf(productService.getProductByAlias(alias = "azur8223_belvti")!!),
                            extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Приорбанк»")
                        ),
                        Solution(alias = "smart_and_card_azur_fm",
                            name = "iKassa Smart&Card 2в1",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            equipment = listOf(productService.getProductByAlias(alias = "azur_fm_belvti")!!),
                            extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Банк БелВЭБ»")
                        ),
                        Solution(alias = "smart_a1",
                            name = "iKassa Smart",
                            legalName = "iKassa Smart",
                            contents = defaultProducts,
                                //equipment = listOf(/*"rpp02n", */"adapter_typec", "adapter_microusb", "adapter_microusb_whippy" /*, "gandlarok_mpos"*/),
                            forcedInstructionPdf = AccompanyingDoc(path="static/forced_a1_smart.pdf", name="00-Инструкция.pdf", raw=true),
                            version = "2.5.0"
                        ),
                        Solution(alias = "smart_and_card_docless_a1",
                            name = "iKassa Smart&Card PAX A930",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            forcedInstructionPdf = AccompanyingDoc(path="static/forced_a1_pax.pdf", name="00-Инструкция.pdf", raw=true)
                        ),
                        Solution(alias = "smart_and_card_a910",
                            name = "iKassa Smart&Card PAX A910",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            equipment =  listOf(productService.getProductByAlias(alias = "pax910")!!),
                            extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Приорбанк»")
                        ),
                        Solution(alias = "smart_and_card_promo",
                            name = "iKassa Smart&Card PAX A930",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            related = listOf(productService.getProductByAlias(alias = "adapter_typec")!!),
                            equipment =  listOf(productService.getProductByAlias(alias = "pax930")!!),
                            extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Банк БелВЭБ»")
                        ),
                        Solution(alias = "smart_and_card_tehnobank",
                            name = "iKassa Smart&Card PAX A930",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                            related = listOf(productService.getProductByAlias(alias = "adapter_typec")!!),
                                //equipment = listOf("pax930"),
                            extraVars = mapOf("PROCESSINGPROVIDER" to "ОАО «Технобанк»")
                        )
                        /**
                         * TODO: легаси-мусор чтоб у НаСвязи не поотваливались формы сразу после обновления.
                         *   удалить на следующем релизе
                         **/
                        ,
                        Solution(
                            alias = "smart_nsv",
                            name = "iKassa Smart",
                            legalName = "iKassa Smart",
                            contents = defaultProducts,
                            version = "2.5.0"
                        ),
                        Solution(
                            alias = "smart_and_card_nsv",
                            name = "iKassa Smart&Card PAX A930",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts,
                        ),
                        Solution(
                            alias = "smart_and_card_nexgo_nsv",
                            name = "iKassa Smart&Card NexGo",
                            legalName = "iKassa Smart&Card",
                            contents = defaultProducts
                        ),
//
//                        /** Солюшен-заглушка, который нельзя выбрать */
                        Solution(
                            alias = "stub",
                            name = "= Выберите решение =",
                            legalName = "",
                            contents = emptyList(),
                            related = emptyList(),
                            equipment = emptyList()
                        ),
                        Solution(
                            alias = "stub_a1",
                            name = "",
                            legalName = "",
                            contents = emptyList(),
                            related = emptyList(),
                            equipment = emptyList()
                        ),

                        /**
                         * Сезонные тарифы
                         * */
                        Solution(
                            alias = "smart_season",
                            name = "iKassa Smart на смартфоне",
                            legalName = "iKassa Smart",
                            contents = listOf(productService.getProductByAlias(alias = "ikassa_license_12_season")!!) +
                                    listOf(productService.getProductByAlias(alias = "skko_register")!!) +
                                    listOf(productService.getProductByAlias(alias = "skko_license_6")!!) +
                                    listOf(productService.getProductByAlias(alias = "personal")!!) +
                                    listOf(productService.getProductByAlias(alias = "app")!!),
//TODO                            equipment =  listOf(productService.getProductByAlias(alias = "smart_docless")!!),
                            version = "2.5.0"
                        ),
                        Solution(
                            alias = "azur_fm_season",
                            name = "iKassa Smart 2в1 на Azur POS SK",
                            legalName = "iKassa Smart",
                            contents = listOf(productService.getProductByAlias(alias = "ikassa_license_12_season")!!) +
                                    listOf(productService.getProductByAlias(alias = "skko_register")!!) +
                                    listOf(productService.getProductByAlias(alias = "skko_license_6")!!) +
                                    listOf(productService.getProductByAlias(alias = "personal")!!) +
                                    listOf(productService.getProductByAlias(alias = "app")!!),
                            equipment =  listOf(productService.getProductByAlias(alias = "azur_fm_belvti")!!),
                            version = "2.5.0"
                        ),
                        Solution(
                            alias = "feitian_f20_season",
                            name = "iKassa Smart&Card на Feitian F20",
                            legalName = "iKassa Smart&Card",
                            contents = listOf(productService.getProductByAlias(alias = "ikassa_license_12_season")!!) +
                                    listOf(productService.getProductByAlias(alias = "skko_register")!!) +
                                    listOf(productService.getProductByAlias(alias = "skko_license_6")!!) +
                                    listOf(productService.getProductByAlias(alias = "personal")!!) +
                                    listOf(productService.getProductByAlias(alias = "app")!!),
                            equipment =  listOf(productService.getProductByAlias(alias = "feitian_f20")!!)
                        )
                )
        )
    }


}