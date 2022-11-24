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

    @Get("/get")
    fun getPartnerFormByUNP(@QueryValue(defaultValue = "193141246") unp: Int): PartnerForm {
        return partnerService.getFormByUNP(unp)
    }

    @Get("/{slug}")
    fun getPartnerFormBySlug(@PathVariable slug: String): PartnerForm {
        return partnerService.getFormByAlias(slug)
    }

    @Get("/delete/{unp}")
    fun deletePartnerForm(@PathVariable unp: Int) {
        return partnerService.deletePartnerForm(unp)
    }

    @Post("/create")
    fun createPartnerForm(@Body requestData: PartnerFormDto) : PartnerForm {
        return partnerService.createPartnerForm(requestData)
    }

    @Post("/update/")
    fun updatePartnerForm(@Body requestData: PartnerFormDto) : PartnerForm {
        return partnerService.updatePartnerForm(requestData)
    }


}