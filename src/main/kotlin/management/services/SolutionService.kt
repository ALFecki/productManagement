package management.services

import jakarta.inject.Singleton
import management.data.products.Solution
import management.data.repositories.SolutionRepository
import management.data.utils.UpdateSolution
import management.forms.AccompanyingDocDto
import management.forms.ProductDto
import management.forms.SolutionDto


@Singleton
class SolutionService(
    private val solutionRepository: SolutionRepository,
    private val productService: ProductService
) {
    fun makeSolution(solution: SolutionDto): Solution {
        val solutionContent = solution.contents.map{ it.alias }
        if (solutionContent.isEmpty() || solutionContent.containsAll(listOf("ikassa_register", "ikassa_license"))) {
            solution.requiredDocs.billingMode = "full"
        } else if (solutionContent.contains("ikassa_register")) {
            solution.requiredDocs.billingMode = "register"
        } else {
            solution.requiredDocs.billingMode = "license"
        }
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
            forcedInstructionPdf = productService.makeAccompanyingDoc(solution.instruction),
            requiredDocs = solution.requiredDocs
        )
    }

    fun makeSolutions(solutions: List<SolutionDto>, isEmpty: Boolean = false): MutableList<Solution> {
        val newSolutions: MutableList<Solution> = mutableListOf()
        if (solutions.isEmpty()) {
            if (isEmpty) {
                return mutableListOf()
            }
            throw IllegalStateException("Not enough data in request")
        }
        solutions.forEach { solution ->
            newSolutions.add(
                solutionRepository.findByAlias(solution.alias)
                    ?: this.makeSolution(solution)
            )
        }
        return newSolutions
    }

    fun getAllSolutions(): MutableList<Solution> {
        return solutionRepository.findAll()
    }

    fun getSolutionByAlias(alias: String): Solution? {
        return solutionRepository.findByAlias(alias)
    }

    fun createSolution(solution: SolutionDto): Solution {
        return solutionRepository.save(makeSolution(solution))
    }

    fun createSolutions(solutions: List<SolutionDto>): MutableList<Solution> {
        return solutionRepository.saveAll(makeSolutions(solutions))
    }

    fun updateSolutionName(alias: String, requestData: UpdateSolution) : Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.name = requestData.name
            ?: throw IllegalStateException("No data in request")
        return solutionRepository.update(solution)
    }

    fun updateSolutionContent(alias: String, content: List<ProductDto>): Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.contents = productService.makeProducts(content)
        return solutionRepository.update(solution)
    }

    fun addSolutionContent(alias: String, content: List<ProductDto>): Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.contents += productService.makeProducts(content)
        return solutionRepository.update(solution)
    }

    fun updateSolutionRelated(alias: String, related: List<ProductDto>): Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.related = productService.makeProducts(related)
        return solutionRepository.update(solution)
    }

    fun addSolutionRelated(alias: String, related: List<ProductDto>): Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.related += productService.makeProducts(related)
        return solutionRepository.update(solution)
    }

    fun updateSolutionPrice(alias: String, requestData: UpdateSolution) : Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.price = requestData.price
            ?: throw IllegalStateException("No data in request")
        return solutionRepository.update(solution)
    }

    fun updateSolutionAccompanyingDoc(alias: String, requestData: List<AccompanyingDocDto>) : Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.accompanyingDoc = productService.makeAccompanyingDocs(requestData)
        return solutionRepository.update(solution)
    }

    fun addSolutionAccompanyingDoc(alias: String, requestData: List<AccompanyingDocDto>) : Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.accompanyingDoc += productService.makeAccompanyingDocs(requestData)
        return solutionRepository.update(solution)
    }

    fun updateSolutionEquipment(alias: String, equipment: List<ProductDto>): Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.equipment = productService.makeProducts(equipment)
        return solutionRepository.update(solution)
    }

    fun addSolutionEquipment(alias: String, equipment: List<ProductDto>): Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.equipment += productService.makeProducts(equipment)
        return solutionRepository.update(solution)
    }

    fun updateSolutionExtraVars(alias: String, requestData: UpdateSolution) : Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.extraVars = requestData.extraVars
            ?: throw IllegalStateException("No data in request")
        return solutionRepository.update(solution)
    }

    fun updateSolutionLegalName(alias: String, requestData: UpdateSolution): Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.legalName = requestData.legalName
            ?: throw IllegalStateException("No data in request")
        return solutionRepository.update(solution)
    }

    fun updateSolutionVersion(alias: String, requestData: UpdateSolution): Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.version = requestData.version
            ?: throw IllegalStateException("No data in request")
        return solutionRepository.update(solution)
    }

    fun updateSolutionInstruction(alias: String, instruction: AccompanyingDocDto): Solution {
        val solution = solutionRepository.findByAlias(alias)
            ?: throw IllegalStateException("No such solution in database")
        solution.forcedInstructionPdf = productService.makeAccompanyingDoc(instruction)
        return solutionRepository.update(solution)
    }

    fun deleteSolution(alias: String) {
        return solutionRepository.deleteByAlias(alias)
    }

}