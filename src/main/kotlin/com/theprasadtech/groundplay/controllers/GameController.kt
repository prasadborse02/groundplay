package com.theprasadtech.groundplay.controllers

import com.theprasadtech.groundplay.domain.dto.GameDto
import com.theprasadtech.groundplay.domain.dto.GameUpdateRequestDto
import com.theprasadtech.groundplay.exceptions.ResourceNotFoundException
import com.theprasadtech.groundplay.services.GameService
import com.theprasadtech.groundplay.toGameDto
import com.theprasadtech.groundplay.toGameEntity
import com.theprasadtech.groundplay.toGameUpdateRequest
import com.theprasadtech.groundplay.utils.logger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class GameController(
    private val gameService: GameService,
) {
    private val log = logger()

    @PostMapping(
        path = ["/game"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    @PreAuthorize("isAuthenticated()")
    fun createGame(
        @Valid @RequestBody gameDto: GameDto,
        authentication: Authentication,
    ): ResponseEntity<GameDto> {
        log.info("Received game creation request: sport=${gameDto.sport}, location=${gameDto.location}")

        // Get current player ID from authentication
        val playerId = authentication.principal as Long

        // Create a copy of the gameDto with the current user as organizer
        val gameEntity =
            gameDto.toGameEntity().copy(
                organizer = playerId,
            )

        val savedGame = gameService.create(gameEntity)
        log.info("Successfully created game with ID: ${savedGame.id} for organizer: $playerId")

        return ResponseEntity(savedGame.toGameDto(), HttpStatus.CREATED)
    }

    @GetMapping("/games/nearby")
    fun getNearbyGames(
        @RequestParam lat: Double,
        @RequestParam lon: Double,
        @RequestParam radiusKm: Double,
    ): ResponseEntity<List<GameDto>> {
        log.info("Searching for games nearby: lat=$lat, lon=$lon, radius=${radiusKm}km")

        val games = gameService.getGamesNearby(lat, lon, radiusKm).map { it.toGameDto() }
        log.info("Found ${games.size} games within ${radiusKm}km radius")

        return ResponseEntity(games, HttpStatus.OK)
    }

    @GetMapping("/games/{id}")
    fun getOrganizersGame(
        @PathVariable id: Long,
    ): ResponseEntity<List<GameDto>> {
        log.info("Fetching games for the organizerId: $id")

        val games = gameService.getByOrganizerId(id).map { it.toGameDto() }
        log.info("Successfully fetched ${games.size} games for organizer: $id")

        return ResponseEntity(games, HttpStatus.OK)
    }

    @GetMapping(
        path = ["/gameDetails/{id}"],
    )
    fun getGameDetails(
        @PathVariable id: Long,
    ): ResponseEntity<GameDto> {
        log.info("Fetching game details for ID: $id")

        val game = gameService.getById(id) ?: throw ResourceNotFoundException("Game", id)
        log.info("Successfully fetched game with ID: $id")

        return ResponseEntity(game.toGameDto(), HttpStatus.OK)
    }

    @PostMapping(
        path = ["/updateGame/{id}"],
    )
    @PreAuthorize("isAuthenticated() and @securityUtil.isGameOrganizer(#id)")
    fun updateGameDetails(
        @PathVariable id: Long,
        @Valid @RequestBody gameUpdateRequestDto: GameUpdateRequestDto,
    ): ResponseEntity<GameDto> {
        log.info("Updating game with ID: $id")

        val updatedGame = gameService.updateGame(id, gameUpdateRequestDto.toGameUpdateRequest())
        log.info("Successfully updated game with ID: $id")

        return ResponseEntity(updatedGame.toGameDto(), HttpStatus.OK)
    }

    @GetMapping("/my-games")
    @PreAuthorize("isAuthenticated()")
    fun getMyGames(authentication: Authentication): ResponseEntity<List<GameDto>> {
        val playerId = authentication.principal as Long
        log.info("Fetching games for current user: $playerId")

        val games = gameService.getByOrganizerId(playerId).map { it.toGameDto() }
        log.info("Successfully fetched ${games.size} games for current user")

        return ResponseEntity(games, HttpStatus.OK)
    }
}
