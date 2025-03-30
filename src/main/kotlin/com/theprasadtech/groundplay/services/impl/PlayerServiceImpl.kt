package com.theprasadtech.groundplay.services.impl

import com.theprasadtech.groundplay.domain.PlayerUpdateRequest
import com.theprasadtech.groundplay.domain.entities.PlayerEntity
import com.theprasadtech.groundplay.repositories.GameMemberRepository
import com.theprasadtech.groundplay.repositories.GameRepository
import com.theprasadtech.groundplay.repositories.PlayerRepository
import com.theprasadtech.groundplay.services.PlayerService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PlayerServiceImpl(
    private val gameRepository: GameRepository,
    private val gameMemberRepository: GameMemberRepository,
    private val playerRepository: PlayerRepository,
) : PlayerService {
    override fun save(playerEntity: PlayerEntity): PlayerEntity {
        require(null == playerEntity.id)
        check(!playerRepository.existsByPhone(playerEntity.phone)) { "Phone number already registered" }
        return playerRepository.save(playerEntity)
    }

    override fun updatePlayer(
        id: Long,
        playerUpdateRequest: PlayerUpdateRequest,
    ): PlayerEntity {
        val existingPlayer = playerRepository.findByIdOrNull(id)
        checkNotNull(existingPlayer)

        val updatedPlayer =
            existingPlayer.copy(
                name = playerUpdateRequest.name ?: existingPlayer.name,
            )

        return playerRepository.save(updatedPlayer)
    }

    override fun getPlayersByGameId(id: Long): List<PlayerEntity> {
        check(gameRepository.existsById(id)) { "Game does not exist !!!" }
        val gameMembers = gameMemberRepository.findByGameId(id)
        val playerIds = gameMembers.map { it.playerId }
        val players = mutableListOf<PlayerEntity>()
        for (playerId in playerIds) {
            check(playerRepository.existsById(playerId))
            val player = playerRepository.findPlayerById(playerId)
            players.add(player)
        }
        return players
    }
}
