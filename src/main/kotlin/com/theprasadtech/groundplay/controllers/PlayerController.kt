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
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PlayerController(
    private val playerService: PlayerService,
) {
    private val logger = logger()

    @PostMapping(path = ["/v1/player"])
    fun createPlayer(
        @Valid @RequestBody playerDto: PlayerDto,
    ): ResponseEntity<PlayerDto> =
        try {
            val savedPlayer = playerService.save(playerDto.toPlayerEntity())
            logger.info("Successfully created a player with ID: ${savedPlayer.id}")
            ResponseEntity(savedPlayer.toPlayerDto(), HttpStatus.CREATED)
        } catch (e: IllegalArgumentException) {
            logger.error("Error creating player: ", e)
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }

    @PatchMapping(path = ["/v1/updatePlayerName/{id}"])
    fun updatePlayerDetails(
        @PathVariable id: Long,
        @RequestBody playerUpdateRequestDto: PlayerUpdateRequestDto,
    ): ResponseEntity<PlayerDto> =
        try {
            val updatedPlayer = playerService.updatePlayer(id, playerUpdateRequestDto.toPlayerUpdateRequest())
            ResponseEntity(updatedPlayer.toPlayerDto(), HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }

    @GetMapping("/v1/{id}/players")
    fun getPlayersByGameId(
        @PathVariable id: Long,
    ): ResponseEntity<List<PlayerDto>> =
        try {
            ResponseEntity(playerService.getPlayersByGameId(id).map { it.toPlayerDto() }, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
}
