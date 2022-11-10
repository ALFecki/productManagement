package management.controllers

import io.micronaut.http.annotation.*
import io.micronaut.json.tree.JsonArray
import io.micronaut.json.tree.JsonNode
import management.data.products.Solution
import management.forms.AccompanyingDocDto
import management.forms.ProductDto
import management.forms.SolutionDto
import management.services.SolutionService


@Controller("/solutions")
class SolutionController(private val solutionService: SolutionService) {

    @Get
    fun getAllSolutions() : MutableList<Solution> {
        return solutionService.getAllSolutions()
    }

    @Get("/{alias}")
    fun getSolutionByAlias(@PathVariable alias : String) : Solution? {
        return solutionService.getSolutionByAlias(alias)
    }

    @Post("/create")
    fun createSolution(@Body solutions : SolutionDto) : Solution {
        return solutionService.createSolution(solutions)
    }

    @Post("/create/list")
    fun createSolutions(@Body solutions : List<SolutionDto>) : MutableList<Solution> {
        return solutionService.createSolutions(solutions)
    }
    @Post("/update/name/{alias}")
    fun updateSolutionName(@PathVariable alias: String, @Body name : Map<String, String>) {
        return solutionService.updateSolutionName(alias, name)
    }

    @Post("/update/price/{alias}")
    fun updateSolutionPrice(@PathVariable alias: String, @Body price : Map<String, String>) {
        return solutionService.updateSolutionPrice(alias, price)
    }

    @Post("/update/legal_name/{alias}")
    fun updateSolutionLegalName(@PathVariable alias: String, @Body legal_name : Map<String, String>) : Solution {
        return solutionService.updateSolutionLegalName(alias, legal_name)
    }

    @Post("/update/version/{alias}")
    fun updateSolutionVersion(@PathVariable alias: String, @Body version : Map<String, String>) : Solution {
        return solutionService.updateSolutionVersion(alias, version)
    }

    @Post("/update/content/{alias}")
    fun updateSolutionVersion(@PathVariable alias: String, @Body content : List<ProductDto>) : Solution {
        return solutionService.updateSolutionContent(alias, content)
    }

    @Post("/update/equipment/{alias}")
    fun updateSolutionEquipment(@PathVariable alias: String, @Body equipment : List<ProductDto>) : Solution {
        return solutionService.updateSolutionEquipment(alias, equipment)
    }

    @Post("/update/related/{alias}")
    fun updateSolutionRelated(@PathVariable alias: String, @Body related : List<ProductDto>) : Solution {
        return solutionService.updateSolutionRelated(alias, related)
    }

    @Post("/update/instruction/{alias}")
    fun updateSolutionInstruction(@PathVariable alias: String, @Body instruction : AccompanyingDocDto) : Solution {
        return solutionService.updateSolutionInstruction(alias, instruction)
    }

    @Post("/add/content/{alias}")
    fun addSolutionVersion(@PathVariable alias: String, @Body content : List<ProductDto>) : Solution {
        return solutionService.addSolutionContent(alias, content)
    }

    @Post("/add/equipment/{alias}")
    fun addSolutionEquipment(@PathVariable alias: String, @Body equipment : List<ProductDto>) : Solution {
        return solutionService.addSolutionEquipment(alias, equipment)
    }

    @Post("/add/related/{alias}")
    fun addSolutionRelated(@PathVariable alias: String, @Body related : List<ProductDto>) : Solution {
        return solutionService.addSolutionRelated(alias, related)
    }
    @Get("/delete/{alias}")
    fun deleteSolutionByAlias(@PathVariable alias: String) {
        return solutionService.deleteSolution(alias)
    }

    @Get("/export/default")
    fun exportDefaultSolutions() : List<Solution> {
        return solutionService.exportDefaultSolutions()
    }
}