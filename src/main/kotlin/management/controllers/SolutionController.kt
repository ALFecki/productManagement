package management.controllers

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Produces
import management.entities.Solution
import management.repositories.SolutionRepository


@Controller("/solutions")
class SolutionController (private val solutionRepository : SolutionRepository){

    @Get
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllSolutions() : List<Solution> {
        val listOfSolutions = solutionRepository.findAll()
            .collectList().block()
        return listOfSolutions
    }

    @Get("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getSolutionByName(@PathVariable name : String) : Solution {
        return solutionRepository.findByName(name)
    }




}