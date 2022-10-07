package management.services

import jakarta.inject.Singleton
import management.entities.Solution
import management.repositories.SolutionRepository


@Singleton
class SolutionService (private val solutionRepository: SolutionRepository) {

    fun getAllSolutions() : MutableList<Solution> {
        return solutionRepository.findAll()
    }

    fun getSolutionByAlias(alias : String) : Solution {
        return solutionRepository.findByAlias(alias)
    }


}