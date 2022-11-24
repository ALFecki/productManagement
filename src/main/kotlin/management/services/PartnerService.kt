package management.services

import jakarta.inject.Singleton
import management.data.products.PartnerForm
import management.data.repositories.PartnerFormRepository
import management.forms.PartnerEmailModeDto
import management.forms.PartnerFormDto


@Singleton
class PartnerService(
    private val partnerFormRepository: PartnerFormRepository,
    private val solutionService: SolutionService
) {

    fun makePartnerForm(data: PartnerFormDto) : PartnerForm {
        return PartnerForm(
            unp = data.unp,
            name = data.name,
            logo = data.logo,
            solutions = solutionService.makeSolutions(data.solutions, true),
            nameRemap = data.nameRemap,
            partnerEmail = data.partnerEmail.toTypedArray(),
            allowManual = data.allowManual,
            formDescription = data.formDescription,
            availablePeriods = data.availablePeriods.toTypedArray(),
            slug = data.slug,
            emailMode = PartnerEmailModeDto.makeEmailMode(data.emailMode)
        )
    }
    fun getAllPartnerForms(): List<PartnerForm> {
        return partnerFormRepository.findAll()
    }

    fun getFormByUNP(UNP: Int): PartnerForm {
        return partnerFormRepository.findByUnp(UNP)
            ?:  throw IllegalStateException("No such form in database")

    }

    fun getFormByAlias(slug: String): PartnerForm {
        return partnerFormRepository.findBySlug(slug)
            ?:  throw IllegalStateException("No such form in database")

    }

    fun createPartnerForm(data: PartnerFormDto) : PartnerForm {
        return partnerFormRepository.save(this.makePartnerForm(data))
    }

    fun updatePartnerForm(data: PartnerFormDto) : PartnerForm {
        if (partnerFormRepository.findByUnp(data.unp) == null) {
            throw IllegalStateException("No such form in database")
        }
        return partnerFormRepository.update(makePartnerForm(data))
    }

    fun deletePartnerForm(unp: Int) {
        return partnerFormRepository.deleteByUnp(unp)
    }
}