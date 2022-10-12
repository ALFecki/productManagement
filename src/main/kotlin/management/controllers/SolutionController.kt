package management.controllers

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import management.entities.Solution
import management.repositories.SolutionRepository
import management.services.SolutionService


@Controller("/solutions")
class SolutionController(private val solutionService: SolutionService) {

    @Get
    fun getAllSolutions() : MutableList<Solution> {
        return solutionService.getAllSolutions()
    }

    @Get("/{alias}")
    fun getSolutionByAlias(@PathVariable alias : String) : Solution {
        return solutionService.getSolutionByAlias(alias)
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