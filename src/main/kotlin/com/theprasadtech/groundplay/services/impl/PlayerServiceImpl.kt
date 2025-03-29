package com.theprasadtech.groundplay.services.impl

import com.theprasadtech.groundplay.domain.entities.PlayerEntity
import com.theprasadtech.groundplay.repositories.PlayerRepository
import com.theprasadtech.groundplay.services.PlayerService
import org.springframework.stereotype.Service

@Service
class PlayerServiceImpl(
    private val playerRepository: PlayerRepository,
) : PlayerService {
    override fun save(playerEntity: PlayerEntity): PlayerEntity {
        playerRepository.findByPhone(playerEntity.phone)?.let {
            throw RuntimeException("Phone Number already exists: ${playerEntity.phone}")
        }
        return playerRepository.save(playerEntity)
    }
}
