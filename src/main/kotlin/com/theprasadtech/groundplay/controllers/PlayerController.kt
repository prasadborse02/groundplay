package com.theprasadtech.groundplay.controllers

import com.theprasadtech.groundplay.domain.dto.PlayerDto
import com.theprasadtech.groundplay.domain.dto.PlayerUpdateRequestDto
import com.theprasadtech.groundplay.services.PlayerService
import com.theprasadtech.groundplay.toPlayerDto
import com.theprasadtech.groundplay.toPlayerEntity
import com.theprasadtech.groundplay.toPlayerUpdateRequest
import com.theprasadtech.groundplay.utils.logger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

    @PostMapping(path = ["/player"])
    fun createPlayer(
        @Valid @RequestBody playerDto: PlayerDto,
    ): ResponseEntity<PlayerDto> {
        log.info("Received player creation request: name=${playerDto.name}, phoneNumber=${playerDto.phoneNumber}")

        val savedPlayer = playerService.save(playerDto.toPlayerEntity())
        log.info("Successfully created player with ID: ${savedPlayer.id}")

        return ResponseEntity(savedPlayer.toPlayerDto(), HttpStatus.CREATED)
    }

    @PostMapping(path = ["/updatePlayerName/{id}"])
    fun updatePlayerDetails(
        @PathVariable id: Long,
        @RequestBody playerUpdateRequestDto: PlayerUpdateRequestDto,
    ): ResponseEntity<PlayerDto> {
        log.info("Updating player with ID: $id")

        val updatedPlayer = playerService.updatePlayer(id, playerUpdateRequestDto.toPlayerUpdateRequest())
        log.info("Successfully updated player with ID: $id")

        return ResponseEntity(updatedPlayer.toPlayerDto(), HttpStatus.OK)
    }

    @GetMapping("/{id}/players/{status}")
    fun getPlayersByGameId(
        @PathVariable id: Long,
        @PathVariable status: String,
    ): ResponseEntity<List<PlayerDto>> {
        log.info("Fetching players for game with ID: $id")

        val players = playerService.getPlayersByGameId(id, status).map { it.toPlayerDto() }
        log.info("Retrieved ${players.size} players for game with ID: $id")

        return ResponseEntity(players, HttpStatus.OK)
    }
}