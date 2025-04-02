package com.theprasadtech.groundplay.services

import com.theprasadtech.groundplay.domain.entities.GameMemberEntity

interface GameMemberService {

    fun enrollPlayer(
        gameId: Long,
        playerId: Long,
    ): GameMemberEntity

    fun unenrollPlayer(
        gameId: Long,
        playerId: Long,
    ): GameMemberEntity
}
