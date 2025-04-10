package com.theprasadtech.groundplay.services

import com.theprasadtech.groundplay.domain.PlayerUpdateRequest
import com.theprasadtech.groundplay.domain.entities.PlayerEntity

interface PlayerService {
    fun save(playerEntity: PlayerEntity): PlayerEntity

    fun updatePlayer(
        id: Long,
        playerUpdateRequest: PlayerUpdateRequest,
    ): PlayerEntity

    fun getPlayersByGameId(
        id: Long,
        status: String,
    ): List<PlayerEntity>
}
