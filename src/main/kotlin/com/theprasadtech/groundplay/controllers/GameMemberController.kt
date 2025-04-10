package com.theprasadtech.groundplay.controllers

import com.theprasadtech.groundplay.domain.dto.GameMemberDto
import com.theprasadtech.groundplay.services.GameMemberService
import com.theprasadtech.groundplay.toGameMemberDto
import com.theprasadtech.groundplay.utils.logger
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class GameMemberController(
    private val gameMemberService: GameMemberService,
) {
    private val log = logger()

    @PostMapping(
        path = ["/games/{gameId}/enroll/{playerId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun enrollPlayer(
        @PathVariable gameId: Long,
        @PathVariable playerId: Long,
    ): ResponseEntity<GameMemberDto> {
        log.info("Enrolling player $playerId in game $gameId")

        val savedEntry = gameMemberService.enrollPlayer(gameId, playerId)
        log.info("Successfully enrolled player ${savedEntry.playerId} in game ${savedEntry.gameId}")

        return ResponseEntity(savedEntry.toGameMemberDto(), HttpStatus.CREATED)
    }

    @PostMapping(
        path = ["/games/{gameId}/unenroll/{playerId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun unenrollPlayer(
        @PathVariable gameId: Long,
        @PathVariable playerId: Long,
    ): ResponseEntity<GameMemberDto> {
        log.info("Unenrolling player $playerId from game $gameId")

        val updatedEntry = gameMemberService.unenrollPlayer(gameId, playerId)
        log.info("Successfully unenrolled player $playerId from game $gameId")

        return ResponseEntity(updatedEntry.toGameMemberDto(), HttpStatus.OK)
    }
}
