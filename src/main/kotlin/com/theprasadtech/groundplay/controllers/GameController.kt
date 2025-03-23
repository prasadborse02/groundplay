package com.theprasadtech.groundplay.controllers

import com.theprasadtech.groundplay.domain.dto.GameDto
import com.theprasadtech.groundplay.services.GameService
import com.theprasadtech.groundplay.toGameDto
import com.theprasadtech.groundplay.toGameEntity
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class GameController(private val gameService: GameService) {
    private val logger = LoggerFactory.getLogger(GameController::class.java)

    @PostMapping(
        path = ["/v1/game"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createGame(@RequestBody gameDto: GameDto): GameDto {
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
}