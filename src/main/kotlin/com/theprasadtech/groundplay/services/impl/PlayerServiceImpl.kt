package com.theprasadtech.groundplay.services.impl

import com.theprasadtech.groundplay.domain.PlayerUpdateRequest
import com.theprasadtech.groundplay.domain.entities.PlayerEntity
import com.theprasadtech.groundplay.exceptions.ResourceAlreadyExistsException
import com.theprasadtech.groundplay.exceptions.ResourceNotFoundException
import com.theprasadtech.groundplay.exceptions.ValidationException
import com.theprasadtech.groundplay.repositories.GameMemberRepository
import com.theprasadtech.groundplay.repositories.GameRepository
import com.theprasadtech.groundplay.repositories.PlayerRepository
import com.theprasadtech.groundplay.services.PlayerService
import com.theprasadtech.groundplay.utils.logger
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PlayerServiceImpl(
    private val gameRepository: GameRepository,
    private val gameMemberRepository: GameMemberRepository,
    private val playerRepository: PlayerRepository,
) : PlayerService {
    private val log = logger()

    @Transactional
    override fun save(playerEntity: PlayerEntity): PlayerEntity {
        log.info("Creating new player: name=${playerEntity.name}, phoneNumber=${playerEntity.phoneNumber}")

        if (playerEntity.id != null) {
            log.error("Cannot create player with predefined ID: ${playerEntity.id}")
            throw ValidationException("New player cannot have a predefined ID")
        }

        if (playerRepository.existsByPhoneNumber(playerEntity.phoneNumber)) {
            log.error("Cannot create player: phone number ${playerEntity.phoneNumber} already registered")
            throw ResourceAlreadyExistsException("Player", "phoneNumber", playerEntity.phoneNumber)
        }

        val savedPlayer = playerRepository.save(playerEntity)
        log.info("Successfully created player with ID: ${savedPlayer.id}")
        return savedPlayer
    }

    @Transactional
    override fun updatePlayer(
        id: Long,
        playerUpdateRequest: PlayerUpdateRequest,
    ): PlayerEntity {
        log.info("Updating player with ID: $id")

        val existingPlayer =
            playerRepository.findByIdOrNull(id)
                ?: throw ResourceNotFoundException("Player", id)

        val updatedPlayer =
            existingPlayer.copy(
                name = playerUpdateRequest.name ?: existingPlayer.name,
            )

        val savedPlayer = playerRepository.save(updatedPlayer)
        log.info("Successfully updated player with ID: $id")
        return savedPlayer
    }

    override fun getPlayersByGameId(
        id: Long,
        status: String,
    ): List<PlayerEntity> {
        log.info("Fetching players for game with ID: $id")

        if (!gameRepository.existsById(id)) {
            log.error("Cannot fetch players: game with ID $id not found")
            throw ResourceNotFoundException("Game", id)
        }

        val gameMembers =
            if (status.lowercase() == "active") {
                gameMemberRepository.findByGameIdAndStatus(id, true)
            } else {
                gameMemberRepository.findByGameIdAndStatus(id, false)
            }

        log.debug("Found $status ${gameMembers.size} game members for game ID: $id")

        val playerIds = gameMembers.map { it.playerId }
        log.debug("Retrieved ${playerIds.size} player IDs from game members")

        // More efficient approach using a single query
        val players = playerRepository.findAllById(playerIds)

        log.info("Successfully retrieved ${players.size} players for game ID: $id")

        if (players.size != playerIds.size) {
            log.warn("Expected ${playerIds.size} players but found ${players.size}. Some players may be missing")
        }

        return players
    }
}
