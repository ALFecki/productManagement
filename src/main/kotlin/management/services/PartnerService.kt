package management.services

import jakarta.inject.Singleton
import management.data.products.PartnerForm
import management.data.products.PartnerFormEmailMode
import management.data.repositories.PartnerFormRepository


@Singleton
class PartnerService(private val partnerFormRepository: PartnerFormRepository,
                     private val solutionService : SolutionService) {

    fun getAllPartnerForms() : List<PartnerForm> {
        return partnerFormRepository.findAll()
    }
    fun getFormByUNP(UNP : Int) : PartnerForm {
        return partnerFormRepository.findByUNP(UNP)
    }

    fun getFormByAlias(slug : String) : PartnerForm {
        return partnerFormRepository.findBySlug(slug)
    }

    fun exportDefault() : List<PartnerForm> {
        return partnerFormRepository.saveAll(
            listOf(
                PartnerForm(
                    UNP = 193141246,
                    name = "iKassa",
                    logo ="https://ikassa.by/ikassa_logo.png",
                    solutions = listOf(
                        solutionService.getSolutionByAlias("smart"),
                        solutionService.getSolutionByAlias("smart_and_card"),
                        solutionService.getSolutionByAlias("smart_and_card_azur"),
                        solutionService.getSolutionByAlias("smart_and_card_nexgo")
                )),
                /**
                 * Для на связи нужны отдельные пакеты документов,
                 * которые по сути только документы на СКО.
                 * Разделение на девайсы нужно только для того,
                 * чтоб файлы назывались правильно и заявки создавались правильно
                 * */
                PartnerForm(
                    UNP = 690017072,
                    name = "На Связи",
                    logo = "https://ikassa.by/partners/ikassa_nsv_form_header.png",
                    solutions =  listOf(
                        solutionService.getSolutionByAlias("smart_docless"),
                        solutionService.getSolutionByAlias("smart_and_card_docless"),
                        solutionService.getSolutionByAlias("smart_and_card_nexgo_docless")
                )),
                PartnerForm(
                    UNP = 101528843,
                    name = "A1",
                    logo ="https://ikassa.by/partners/ikassa_a1_form_header.png",
                    solutions = listOf(
                        solutionService.getSolutionByAlias("stub_a1"),
                        solutionService.getSolutionByAlias("smart_and_card_docless_a1"),
                        solutionService.getSolutionByAlias("smart_a1")
                    ),
                    nameRemap =  hashMapOf(
                        "smart_and_card_docless_a1" to "Smart-касса PAX A930",
                        "fm_docless" to "Smart-касса PayMob M-1",
                        "smart_a1" to "Smart-касса на смартфоне"
                    )
                ),
                PartnerForm(
                    UNP =100220190,
                    name = "ПриорБанк",
                    logo = "https://ikassa.by/partners/ikassa_prior_form_header.png",
                    solutions = listOf(solutionService.getSolutionByAlias("smart"))
                ),
                PartnerForm(
                    UNP = 190557126,
                    name = "Системные решения",
                    logo ="https://ikassa.by/partners/ikassa_sr_form_header.png",
                    solutions =  listOf(
                        solutionService.getSolutionByAlias("dusik_r"),
                        solutionService.getSolutionByAlias("smart_docless"),
                        solutionService.getSolutionByAlias("smart_and_card_docless"),
                        solutionService.getSolutionByAlias("smart_and_card_a910_docless"),
                        solutionService.getSolutionByAlias("smart_and_card_azur_docless")
                    )
                ),
                PartnerForm(
                    UNP = 192656913,
                    name ="r_keeper",
                    logo ="https://ikassa.by/partners/ikassa_r_keeper_form_header.png",
                    solutions =  listOf(solutionService.getSolutionByAlias("dusik_r_partner"))
                ),
                PartnerForm(
                    UNP = 193389921,
                    name = "Дракарис",
                    logo ="https://ikassa.by/ikassa_logo.png",
                    listOf(solutionService.getSolutionByAlias("dusik_r_partner")),
                    emailModeName = PartnerFormEmailMode.NONE.name
                ),
                PartnerForm(
                    UNP = 191826858,
                    name = "RestFront",
                    logo = "https://ikassa.by/partners/ikassa_restfront_form_header.png",
                    solutions =  listOf(solutionService.getSolutionByAlias("dusik_r")),
                    nameRemap = hashMapOf("smart" to "iKassa Prior SoftPos")
                ),
                PartnerForm(
                    UNP = 191263307,
                    name ="Bytech",
                    logo ="https://ikassa.by/ikassa_logo.png",
                    solutions =  listOf(solutionService.getSolutionByAlias("smart_and_card_azur")),
                    partnerEmail = arrayOf("sales@bytechs.by"),
                    slug = "bytech_azur"
                ),
                PartnerForm(
                    UNP =191263307,
                    name = "Bytech",
                    logo = "https://ikassa.by/ikassa_logo.png",
                    solutions =  listOf(solutionService.getSolutionByAlias("smart_and_card_a910")),
                    partnerEmail = arrayOf("sales@bytechs.by"),
                    slug = "bytech_pax910"
                ),
                PartnerForm(
                    UNP = 190374449,
                    name = "Ланкард",
                    logo ="https://ikassa.by/partners/ikassa_lancard_form_header.png",
                    solutions =  listOf(
                        solutionService.getSolutionByAlias("smart_and_card_belweb"),
                        solutionService.getSolutionByAlias("smart_and_card_belgazprom"),
                        solutionService.getSolutionByAlias("smart_and_card_belinvest"),
                        solutionService.getSolutionByAlias("smart_and_card_belarusbank"),
                        solutionService.getSolutionByAlias("smart_and_card_rrbbank"),
                        solutionService.getSolutionByAlias("smart_and_card_paritetbank"),
                        solutionService.getSolutionByAlias("smart_and_card_belagroprombank"),
                    ),
                    partnerEmail = arrayOf("pos@lancard.by", "kudelko.e@farnell-service.by")
                ),
                PartnerForm(
                    UNP = 100361187,
                    name = "РРБ-БАНК",
                    logo = "https://ikassa.by/partners/ikassa_bankrrb_form_header.png",
                    solutions =  listOf(
                        solutionService.getSolutionByAlias("smart_and_card_rrbbank"),
                        solutionService.getSolutionByAlias("smart")
                    ),
                    partnerEmail = arrayOf("pos@lancard.by", "kudelko.e@farnell-service.by")
                ),
                PartnerForm(
                    UNP = 100233809,
                    name = "Паритетбанк",
                    logo ="https://ikassa.by/partners/ikassa_paritetbank_form_header.png",
                    solutions =  listOf(
                        solutionService.getSolutionByAlias("smart_and_card_paritetbank")
                    ),
                    partnerEmail = arrayOf("pos@lancard.by", "kudelko.e@farnell-service.by")
                ),
                PartnerForm(
                    UNP = 101468222,
                    name = "БелВТИ",
                    logo ="https://ikassa.by/partners/ikassa_belvti_form_header.png",
                    solutions = listOf(
                        solutionService.getSolutionByAlias("smart_and_card_azur_belweb"),
                        solutionService.getSolutionByAlias("smart_and_card_azur_prior"),
                        solutionService.getSolutionByAlias("smart_and_card_azur_fm")
                    ),
                    allowManual = false,
                    partnerEmail = arrayOf("region@belvti.by")
                ),
                PartnerForm(
                    UNP = 100706562,
                    name = "Технобанк",
                    logo ="https://object.ikassa.by/ikassa-assets/ikassa_tehnobank_form_header.png",
                    solutions =  listOf(
                        solutionService.getSolutionByAlias("smart_and_card_tehnobank")
                    ),
                    //allowManual = false,
                    partnerEmail = arrayOf("a.ivanischev@tb.by")
                ),
                PartnerForm(
                    UNP =2022,
                    name ="Сезон Smart",
                    logo = "https://ikassa.by/ikassa_logo.png",
                    solutions =  listOf(
                        solutionService.getSolutionByAlias("smart_season"),
                        solutionService.getSolutionByAlias("azur_fm_season"),
                        solutionService.getSolutionByAlias("feitian_f20_season")
                    ),
                    availablePeriods = arrayOf(12),
                    formDescription = "Эта форма позволяет заполнить данные и скачать готовый пакет документов для тарифа \"Сезон Smart\""
                ),
                PartnerForm(
                    UNP = 100191549,
                    name ="Берлио",
                    logo = "https://ikassa.by/partners/ikassa_berlio_form_header.png",
                    solutions = listOf(solutionService.getSolutionByAlias("dusik_r_partner")),

                    emailModeName = PartnerFormEmailMode.BOTH.name,
                    availablePeriods = arrayOf(1)
                ),
                PartnerForm(
                    UNP = 190369806,
                    name = "ККС",
                    logo = "https://object.ikassa.by/ikassa-assets/ikassa_kks.png",
                    solutions =  listOf(solutionService.getSolutionByAlias("dusik_r_partner_partner")),
                    emailModeName = PartnerFormEmailMode.BOTH.name,
                    availablePeriods = arrayOf(1)
                ),
                PartnerForm(
                    UNP = 190497010,
                    name ="ООО \"СОФТ-СИСТЕМ\"",
                    logo ="https://object.ikassa.by/ikassa-assets/ikassa_softsystems.png",
                    solutions =  listOf(solutionService.getSolutionByAlias("dusik_r_partner")),
                    emailModeName = PartnerFormEmailMode.BOTH.name,
                    partnerEmail = arrayOf("info@softsys.by")
                ),
            )
        )
    }
}