package management.controllers

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import jakarta.inject.Singleton
import management.data.products.PartnerForm
import management.services.PartnerService


@Controller("/partner")
class PartnerController (private val partnerService: PartnerService) {

    @Get
    fun getAllPartnerForms() : List<PartnerForm> {
        return partnerService.getAllPartnerForms()
    }

    @Get("/{unp}")
    fun getPartnerFormByUNP(@PathVariable unp : Int) : PartnerForm {
        return partnerService.getFormByUNP(unp)
    }

    @Get("/{slug}")
    fun getPartnerFormBySlug(@PathVariable slug : String) : PartnerForm {
        return partnerService.getFormByAlias(slug)
    }

    @Get("/export/default")
    fun exportDefaultForms() : List<PartnerForm> {
        return partnerService.exportDefault()
    }

}