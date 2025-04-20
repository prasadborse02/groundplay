package com.theprasadtech.groundplay.controllers

import com.theprasadtech.groundplay.domain.dto.PlayerDto
import com.theprasadtech.groundplay.domain.dto.PlayerUpdateRequestDto
import com.theprasadtech.groundplay.exceptions.ResourceNotFoundException
import com.theprasadtech.groundplay.services.PlayerService
import com.theprasadtech.groundplay.toPlayerDto
import com.theprasadtech.groundplay.toPlayerUpdateRequest
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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class PlayerController(
    private val playerService: PlayerService,
) {
    private val log = logger()

    @PostMapping(
        path = ["/players/{playerId}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    @PreAuthorize("isAuthenticated() and @securityUtil.isCurrentPlayer(#playerId)")
    fun updatePlayerDetails(
        @PathVariable playerId: Long,
        @Valid @RequestBody playerUpdateRequestDto: PlayerUpdateRequestDto,
    ): ResponseEntity<PlayerDto> {
        log.info("Updating player with ID: $playerId")

        val updatedPlayer = playerService.updatePlayer(playerId, playerUpdateRequestDto.toPlayerUpdateRequest())
        log.info("Successfully updated player with ID: $playerId")

        return ResponseEntity(updatedPlayer.toPlayerDto(), HttpStatus.OK)
    }

    @PostMapping(
        path = ["/my-profile"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    @PreAuthorize("isAuthenticated()")
    fun updateCurrentPlayerDetails(
        @Valid @RequestBody playerUpdateRequestDto: PlayerUpdateRequestDto,
        authentication: Authentication,
    ): ResponseEntity<PlayerDto> {
        val playerId = authentication.principal as Long
        log.info("Updating current player with ID: $playerId")

        val updatedPlayer = playerService.updatePlayer(playerId, playerUpdateRequestDto.toPlayerUpdateRequest())
        log.info("Successfully updated current player with ID: $playerId")

        return ResponseEntity(updatedPlayer.toPlayerDto(), HttpStatus.OK)
    }

    @GetMapping("/my-profile")
    @PreAuthorize("isAuthenticated()")
    fun getCurrentPlayerProfile(authentication: Authentication): ResponseEntity<PlayerDto> {
        val playerId = authentication.principal as Long
        log.info("Fetching profile for current player with ID: $playerId")

        val player = playerService.getById(playerId) ?: throw ResourceNotFoundException("Player", playerId)
        log.info("Successfully fetched profile for current player with ID: $playerId")

        return ResponseEntity(player.toPlayerDto(), HttpStatus.OK)
    }

    @GetMapping("/players/{playerId}")
    fun getPlayerProfile(
        @PathVariable playerId: Long,
    ): ResponseEntity<PlayerDto> {
        log.info("Fetching profile for player with ID: $playerId")

        val player = playerService.getById(playerId) ?: throw ResourceNotFoundException("Player", playerId)
        log.info("Successfully fetched profile for player with ID: $playerId")

        return ResponseEntity(player.toPlayerDto(), HttpStatus.OK)
    }

    @GetMapping("/games/{gameId}/players/{status}")
    @PreAuthorize("isAuthenticated()")
    fun getPlayersByGameId(
        @PathVariable gameId: Long,
        @PathVariable status: String,
    ): ResponseEntity<List<PlayerDto>> {
        log.info("Fetching players for game with ID: $gameId with status: $status")

        val players = playerService.getPlayersByGameId(gameId, status).map { it.toPlayerDto() }
        log.info("Retrieved ${players.size} players for game with ID: $gameId")

        return ResponseEntity(players, HttpStatus.OK)
    }
}
