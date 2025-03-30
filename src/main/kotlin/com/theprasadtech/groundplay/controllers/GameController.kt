package com.theprasadtech.groundplay.controllers

import com.theprasadtech.groundplay.domain.dto.GameDto
import com.theprasadtech.groundplay.domain.dto.GameUpdateRequestDto
import com.theprasadtech.groundplay.domain.entities.GameEntity
import com.theprasadtech.groundplay.services.GameService
import com.theprasadtech.groundplay.toGameDto
import com.theprasadtech.groundplay.toGameEntity
import com.theprasadtech.groundplay.toGameUpdateRequest
import com.theprasadtech.groundplay.utils.logger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
    ): ResponseEntity<GameDto> {
        logger.info("Received game creation request: $gameDto")
        // TODO: Add another exception for missing arguments
        return try {
            val savedGame = gameService.create(gameDto.toGameEntity())
            logger.info("Successfully created game with ID: ${savedGame.id}")
            ResponseEntity(savedGame.toGameDto(), HttpStatus.CREATED)
        } catch (e: IllegalArgumentException) {
            logger.error("Error creating game: ", e)
            ResponseEntity(HttpStatus.CONFLICT)
        }
    }

    @GetMapping("/v1/games/nearby")
    fun getNearbyGames(
        @RequestParam lat: Double,
        @RequestParam lon: Double,
        @RequestParam radiusKm: Double,
    ): ResponseEntity<List<GameEntity>> =
        try {
            ResponseEntity(gameService.getGamesNearby(lat, lon, radiusKm), HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }

    @GetMapping(
        path = ["/v1/gameDetails/{id}"],
    )
    fun getGameDetails(
        @PathVariable id: Long,
    ): ResponseEntity<GameDto> {
        logger.info("Fetching game details for ID: $id")
        val game = gameService.getById(id)?.toGameDto()
        return game?.let {
            logger.info("Successfully fetched game with ID: $id")
            ResponseEntity(it, HttpStatus.OK)
        } ?: run {
            logger.error("Game not found with ID: $id")
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PatchMapping(
        path = ["/v1/updateGame/{id}"],
    )
    fun updateGameDetails(
        @PathVariable id: Long,
        @Valid @RequestBody gameUpdateRequestDto: GameUpdateRequestDto,
    ): ResponseEntity<GameDto> =
        try {
            val updatedGame = gameService.updateGame(id, gameUpdateRequestDto.toGameUpdateRequest())
            ResponseEntity(updatedGame.toGameDto(), HttpStatus.OK)
        } catch (e: IllegalStateException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
}
