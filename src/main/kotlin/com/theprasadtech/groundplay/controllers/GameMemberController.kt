package com.theprasadtech.groundplay.controllers

import com.theprasadtech.groundplay.domain.dto.GameMemberDto
import com.theprasadtech.groundplay.security.SecurityUtil
import com.theprasadtech.groundplay.services.GameMemberService
import com.theprasadtech.groundplay.toGameMemberDto
import com.theprasadtech.groundplay.utils.logger
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class GameMemberController(
    private val gameMemberService: GameMemberService,
) {
    private val log = logger()

    @PostMapping(
        path = ["/games/{gameId}/enroll"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    @PreAuthorize("isAuthenticated()")
    fun enrollCurrentPlayer(
        @PathVariable gameId: Long,
        authentication: Authentication,
    ): ResponseEntity<GameMemberDto> {
        val playerId = authentication.principal as Long
        log.info("Enrolling current player $playerId in game $gameId")

        val savedEntry = gameMemberService.enrollPlayer(gameId, playerId)
        log.info("Successfully enrolled player ${savedEntry.playerId} in game ${savedEntry.gameId}")

        return ResponseEntity(savedEntry.toGameMemberDto(), HttpStatus.CREATED)
    }

    @PostMapping(
        path = ["/games/{gameId}/unenroll"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    @PreAuthorize("isAuthenticated()")
    fun unenrollCurrentPlayer(
        @PathVariable gameId: Long,
        authentication: Authentication,
    ): ResponseEntity<GameMemberDto> {
        val playerId = authentication.principal as Long
        log.info("Unenrolling current player $playerId from game $gameId")

        val updatedEntry = gameMemberService.unenrollPlayer(gameId, playerId)
        log.info("Successfully unenrolled player $playerId from game $gameId")

        return ResponseEntity(updatedEntry.toGameMemberDto(), HttpStatus.OK)
    }

    @GetMapping(
        path = ["/my-enrolled-games"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    @PreAuthorize("isAuthenticated()")
    fun getMyEnrolledGames(
        authentication: Authentication,
        @RequestParam(required = false, defaultValue = "true") activeOnly: Boolean,
    ): ResponseEntity<List<GameMemberDto>> {
        val playerId = authentication.principal as Long
        log.info("Fetching enrolled games for player $playerId, activeOnly=$activeOnly")

        val enrollments = gameMemberService.getPlayerEnrollments(playerId, activeOnly)
        log.info("Found ${enrollments.size} enrolled games for player $playerId")

        return ResponseEntity.ok(enrollments.map { it.toGameMemberDto() })
    }

    @GetMapping("/players/{playerId}/enrollments")
    @PreAuthorize("isAuthenticated() and (@securityUtil.isCurrentPlayer(#playerId)")
    fun getPlayerEnrollments(
        @PathVariable playerId: Long,
        @RequestParam(required = false, defaultValue = "true") activeOnly: Boolean,
    ): ResponseEntity<List<GameMemberDto>> {
        log.info("Fetching enrollments for player $playerId, activeOnly=$activeOnly")

        val enrollments = gameMemberService.getPlayerEnrollments(playerId, activeOnly)
        log.info("Found ${enrollments.size} enrollments for player $playerId")

        return ResponseEntity.ok(enrollments.map { it.toGameMemberDto() })
    }
}
