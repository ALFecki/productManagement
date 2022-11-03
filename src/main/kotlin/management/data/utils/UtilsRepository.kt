package management.data.utils

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository


@Repository
interface UtilsRepository : JpaRepository<Util, Long> {

    fun findByName(name : String) : Util?

}