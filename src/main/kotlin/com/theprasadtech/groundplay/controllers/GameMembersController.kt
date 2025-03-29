package com.theprasadtech.groundplay.controllers

import com.theprasadtech.groundplay.services.GameMemberService
import org.springframework.web.bind.annotation.RestController

@RestController
class GameMembersController(
    private val gameMemberService: GameMemberService,
) {
    // TODO: Register player for a game

    // TODO: Get All the players from the game

    // TODO: validate and discard entry of a player from a game
}
