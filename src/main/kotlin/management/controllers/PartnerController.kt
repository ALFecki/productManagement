package management.controllers

import io.micronaut.http.annotation.*
import management.data.products.PartnerForm
import management.forms.PartnerFormDto
import management.services.PartnerService


@Controller("/partner")
class PartnerController(private val partnerService: PartnerService) {

    @Get
    fun getAllPartnerForms(): List<PartnerForm> {
        return partnerService.getAllPartnerForms()
    }

    @Get("/{unp}")
    fun getPartnerFormByUNP(@PathVariable unp: Int): PartnerForm {
        return partnerService.getFormByUNP(unp)
    }

    @Get("/{slug}")
    fun getPartnerFormBySlug(@PathVariable slug: String): PartnerForm {
        return partnerService.getFormByAlias(slug)
    }

    @Post("/create")
    fun createPartnerForm(@Body requestData: PartnerFormDto) : PartnerForm {
        return partnerService.createPartnerForm(requestData)
    }

}