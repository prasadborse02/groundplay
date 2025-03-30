package com.theprasadtech.groundplay.services.impl

import com.theprasadtech.groundplay.domain.PlayerUpdateRequest
import com.theprasadtech.groundplay.domain.entities.PlayerEntity
import com.theprasadtech.groundplay.repositories.PlayerRepository
import com.theprasadtech.groundplay.services.PlayerService
import org.springframework.data.repository.findByIdOrNull
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

    override fun updatePlayer(id: Long, playerUpdateRequest: PlayerUpdateRequest): PlayerEntity {
        val existingPlayer = playerRepository.findByIdOrNull(id)
        checkNotNull(existingPlayer)

        val updatedPlayer =
            existingPlayer.copy(
                name = playerUpdateRequest.name ?: existingPlayer.name,
            )

        return playerRepository.save(updatedPlayer)
    }
}
