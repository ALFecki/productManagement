package management.data.repositories

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import management.data.entities.PartnerForm


@Repository
interface PartnerFormRepository : JpaRepository<PartnerForm, Long> {
    fun findByUNP(UNP : Int) : PartnerForm
    fun findBySlug(slug : String) : PartnerForm
}