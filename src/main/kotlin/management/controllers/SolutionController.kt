package management.controllers

import io.micronaut.http.annotation.*
import management.data.products.Solution
import management.data.utils.UpdateSolution
import management.forms.AccompanyingDocDto
import management.forms.ProductDto
import management.forms.SolutionDto
import management.services.SolutionService


@Controller("/solutions")
class SolutionController(private val solutionService: SolutionService) {

    @Get
    fun getAllSolutions(): MutableList<Solution> {
        return solutionService.getAllSolutions()
    }

    @Get("/{alias}")
    fun getSolutionByAlias(@PathVariable alias: String): Solution? {
        return solutionService.getSolutionByAlias(alias)
    }

    @Post("/create")
    fun createSolution(@Body solutions: SolutionDto): Solution {
        return solutionService.createSolution(solutions)
    }

    @Post("/create/list")
    fun createSolutions(@Body solutions: List<SolutionDto>): MutableList<Solution> {
        return solutionService.createSolutions(solutions)
    }

    @Post("/update/name/{alias}")
    fun updateSolutionName(@PathVariable alias: String, @Body requestData: UpdateSolution) : Solution {
        return solutionService.updateSolutionName(alias, requestData)
    }

    @Post("/update/content/{alias}")
    fun updateSolutionContent(@PathVariable alias: String, @Body content: List<ProductDto>): Solution {
        return solutionService.updateSolutionContent(alias, content)
    }

    @Post("/add/content/{alias}")
    fun addSolutionContent(@PathVariable alias: String, @Body content: List<ProductDto>): Solution {
        return solutionService.addSolutionContent(alias, content)
    }

    @Post("/update/related/{alias}")
    fun updateSolutionRelated(@PathVariable alias: String, @Body related: List<ProductDto>): Solution {
        return solutionService.updateSolutionRelated(alias, related)
    }

    @Post("/add/related/{alias}")
    fun addSolutionRelated(@PathVariable alias: String, @Body related: List<ProductDto>): Solution {
        return solutionService.addSolutionRelated(alias, related)
    }

    @Post("/update/price/{alias}")
    fun updateSolutionPrice(@PathVariable alias: String, @Body requestData: UpdateSolution) : Solution {
        return solutionService.updateSolutionPrice(alias, requestData)
    }

    @Post("/update/accompanying_doc/{alias}")
    fun updateSolutionAccompanyingDoc(@PathVariable alias: String, @Body requestData: List<AccompanyingDocDto>) : Solution {
        return solutionService.updateSolutionAccompanyingDoc(alias, requestData)
    }

    @Post("/add/accompanying_doc/{alias}")
    fun addSolutionAccompanyingDoc(@PathVariable alias: String, @Body requestData: List<AccompanyingDocDto>) : Solution {
        return solutionService.addSolutionAccompanyingDoc(alias, requestData)
    }

    @Post("/update/equipment/{alias}")
    fun updateSolutionEquipment(@PathVariable alias: String, @Body equipment: List<ProductDto>): Solution {
        return solutionService.updateSolutionEquipment(alias, equipment)
    }

    @Post("/add/equipment/{alias}")
    fun addSolutionEquipment(@PathVariable alias: String, @Body equipment: List<ProductDto>): Solution {
        return solutionService.addSolutionEquipment(alias, equipment)
    }

    @Post("update/extra_vars/{alias}")
    fun updateSolutionExtraVars(@PathVariable alias: String, @Body requestData: UpdateSolution) : Solution {
        return solutionService.updateSolutionExtraVars(alias, requestData)
    }

    @Post("/update/legal_name/{alias}")
    fun updateSolutionLegalName(@PathVariable alias: String, @Body requestData: UpdateSolution): Solution {
        return solutionService.updateSolutionLegalName(alias, requestData)
    }

    @Post("/update/version/{alias}")
    fun updateSolutionVersion(@PathVariable alias: String, @Body requestData: UpdateSolution): Solution {
        return solutionService.updateSolutionVersion(alias, requestData)
    }

    @Post("/update/instruction/{alias}")
    fun updateSolutionInstruction(@PathVariable alias: String, @Body instruction: AccompanyingDocDto): Solution {
        return solutionService.updateSolutionInstruction(alias, instruction)
    }

    @Get("/delete/{alias}")
    fun deleteSolutionByAlias(@PathVariable alias: String) {
        return solutionService.deleteSolution(alias)
    }
}