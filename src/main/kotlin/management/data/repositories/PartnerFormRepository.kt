package management.data.repositories

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import management.data.products.PartnerForm


@Repository
interface PartnerFormRepository : JpaRepository<PartnerForm, Long> {
    fun findByUnp(UNP: Int): PartnerForm?
    fun findBySlug(slug: String): PartnerForm?

    fun deleteByUnp(unp: Int)
}