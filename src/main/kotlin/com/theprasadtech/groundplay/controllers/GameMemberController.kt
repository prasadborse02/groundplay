package com.theprasadtech.groundplay.controllers

import com.theprasadtech.groundplay.domain.dto.GameMemberDto
import com.theprasadtech.groundplay.services.GameMemberService
import com.theprasadtech.groundplay.toGameMemberDto
import com.theprasadtech.groundplay.toGameMemberEntity
import com.theprasadtech.groundplay.utils.logger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class GameMemberController(
    private val gameMemberService: GameMemberService,
) {
    private val logger = logger()

    @PostMapping(
        path = ["/v1/register"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun registerPlayerForGame(
        @Valid @RequestBody gameMemberDto: GameMemberDto,
    ): ResponseEntity<GameMemberDto> =
        try {
            val savedEntry = gameMemberService.create(gameMemberDto.toGameMemberEntity())
            // TODO: Update the count of registredPlayers
            ResponseEntity(savedEntry.toGameMemberDto(), HttpStatus.CREATED)
        } catch (e: IllegalArgumentException) {
            logger.error("Error registering player for a game: ", e)
            ResponseEntity(HttpStatus.CONFLICT)
        }
}
