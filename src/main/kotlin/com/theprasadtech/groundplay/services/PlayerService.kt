package com.theprasadtech.groundplay.services

import com.theprasadtech.groundplay.domain.entities.PlayerEntity

interface PlayerService {
    fun save(playerEntity: PlayerEntity): PlayerEntity
}
