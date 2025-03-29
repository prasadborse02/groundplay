package com.theprasadtech.groundplay.controllers

import com.theprasadtech.groundplay.domain.dto.GameDto
import com.theprasadtech.groundplay.services.GameService
import com.theprasadtech.groundplay.toGameDto
import com.theprasadtech.groundplay.toGameEntity
import com.theprasadtech.groundplay.utils.logger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class GameController(
    private val gameService: GameService,
) {
    private val logger = logger()

    @PostMapping(
        path = ["/v1/game"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun createGame(
        @Valid @RequestBody gameDto: GameDto,
    ): GameDto {
        logger.info("Received game creation request: $gameDto")
        try {
            val savedGame = gameService.save(gameDto.toGameEntity())
            logger.info("Successfully created game with ID: ${savedGame.id}")
            return savedGame.toGameDto()
        } catch (e: Exception) {
            logger.error("Error creating game: ", e)
            throw e
        }
    }

    // TODO: List All active games within radius of lat long

    // TODO: Get game details by Id
    @GetMapping(
        path = ["/v1/gameDetails/{id}"],
    )
    fun getGameDetails(
        @PathVariable id: Long,
    ): GameDto {
        try {
            val gameDetails = gameService.getById(id)
            logger.info("Successfully fetched game with ID: $id")
            return gameDetails.toGameDto()
        } catch (e: NoSuchElementException) {
            logger.error("Game not found with ID: $id")
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found", e)
        } catch (e: Exception) {
            logger.error("Error while fetching game: ", e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching game details", e)
        }
    }

    // TODO: Validate & Update game details such as - startTime, endTime, Sport, Location, Corodinates

    // TODO: Inactive the game by Id by organizer
}
