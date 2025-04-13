package com.theprasadtech.groundplay.services.impl

import com.theprasadtech.groundplay.domain.entities.GameMemberEntity
import com.theprasadtech.groundplay.exceptions.GameFullException
import com.theprasadtech.groundplay.exceptions.PlayerNotAvailableException
import com.theprasadtech.groundplay.exceptions.ResourceNotFoundException
import com.theprasadtech.groundplay.exceptions.ValidationException
import com.theprasadtech.groundplay.repositories.GameMemberRepository
import com.theprasadtech.groundplay.repositories.GameRepository
import com.theprasadtech.groundplay.repositories.PlayerRepository
import com.theprasadtech.groundplay.services.GameMemberService
import com.theprasadtech.groundplay.utils.logger
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GameMemberServiceImpl(
    private val gameRepository: GameRepository,
    private val gameMemberRepository: GameMemberRepository,
    private val playerRepository: PlayerRepository,
) : GameMemberService {
    private val log = logger()

    @Transactional
    override fun enrollPlayer(
        gameId: Long,
        playerId: Long,
    ): GameMemberEntity {
        log.info("Enrolling player $playerId in game $gameId")

        // Validate game and player exist
        validateGameAndPlayerExist(gameId, playerId)

        // Validate game time and availability
        validateGameTimeAndAvailability(gameId, playerId)

        // Check if player is already enrolled
        if (gameMemberRepository.existsByGameIdAndPlayerId(gameId, playerId)) {
            log.info("Player $playerId is already in database for game $gameId, updating status to enrolled")

            val existingEntity = gameMemberRepository.findByGameIdAndPlayerId(gameId, playerId)
            val updatedEntity = existingEntity.copy(status = true)

            val savedEntity = gameMemberRepository.save(updatedEntity)
            log.info("Successfully updated enrollment status for player $playerId in game $gameId")
            return savedEntity
        } else {
            // Check player availability for new enrollment
            val gameDetails = gameRepository.findGameById(gameId)
            if (!gameMemberRepository.isPlayerAvailable(gameDetails.startTime, gameDetails.endTime, playerId)) {
                log.error("Cannot enroll player: player $playerId is not available during game time")
                throw PlayerNotAvailableException(listOf(playerId))
            }

            val newEntity =
                GameMemberEntity(
                    id = null,
                    gameId = gameId,
                    playerId = playerId,
                    status = true,
                )

            val savedEntity = gameMemberRepository.save(newEntity)
            log.info("Successfully enrolled player $playerId in game $gameId")
            return savedEntity
        }
    }

    @Transactional
    override fun unenrollPlayer(
        gameId: Long,
        playerId: Long,
    ): GameMemberEntity {
        log.info("Unenrolling player $playerId from game $gameId")

        // Validate game and player exist
        validateGameAndPlayerExist(gameId, playerId)

        // Check if player is enrolled
        if (gameMemberRepository.existsByGameIdAndPlayerId(gameId, playerId)) {
            val existingEntity = gameMemberRepository.findByGameIdAndPlayerId(gameId, playerId)

            // If already unenrolled, just return the existing entity
            if (!existingEntity.status) {
                log.info("Player $playerId is already unenrolled from game $gameId, no action needed")
                return existingEntity
            }

            val updatedEntity = existingEntity.copy(status = false)
            val savedEntity = gameMemberRepository.save(updatedEntity)
            log.info("Successfully unenrolled player $playerId from game $gameId")
            return savedEntity
        } else {
            log.warn("Player $playerId is not enrolled in game $gameId, creating unenrolled record")

            // Create a new record with status=false
            val newEntity =
                GameMemberEntity(
                    id = null,
                    gameId = gameId,
                    playerId = playerId,
                    status = false,
                )

            val savedEntity = gameMemberRepository.save(newEntity)
            log.info("Created unenrolled record for player $playerId in game $gameId")
            return savedEntity
        }
    }

    override fun getPlayerEnrollments(
        playerId: Long,
        activeOnly: Boolean,
    ): List<GameMemberEntity> {
        log.info("Getting enrollments for player $playerId, activeOnly=$activeOnly")

        // Validate player exists
        if (!playerRepository.existsById(playerId)) {
            log.error("Player with ID $playerId not found")
            throw ResourceNotFoundException("Player", playerId)
        }

        return if (activeOnly) {
            gameMemberRepository.findByPlayerIdAndStatus(playerId, true)
        } else {
            gameMemberRepository.findByPlayerId(playerId)
        }
    }

    // Implementation of isGameOrganizer method
    override fun isGameOrganizer(
        gameId: Long,
        playerId: Long,
    ): Boolean {
        log.debug("Checking if player $playerId is the organizer of game $gameId")

        val game = gameRepository.findById(gameId)

        if (game.isEmpty) {
            log.debug("Game not found with ID: $gameId")
            return false
        }

        val isOrganizer = game.get().organizer == playerId

        if (!isOrganizer) {
            log.debug("Player $playerId is not the organizer of game $gameId")
        }

        return isOrganizer
    }

    // Helper methods
    private fun validateGameAndPlayerExist(
        gameId: Long,
        playerId: Long,
    ) {
        // Validate game exists
        if (!gameRepository.existsById(gameId)) {
            log.error("Game with ID $gameId not found")
            throw ResourceNotFoundException("Game", gameId)
        }

        // Validate player exists
        if (!playerRepository.existsById(playerId)) {
            log.error("Player with ID $playerId not found")
            throw ResourceNotFoundException("Player", playerId)
        }
    }

    private fun validateGameTimeAndAvailability(
        gameId: Long,
        playerId: Long,
    ) {
        val gameDetails = gameRepository.findGameById(gameId)

        // Validate game is in the future
        if (!gameDetails.startTime.isAfter(LocalDateTime.now())) {
            log.error("Cannot enroll player: game with ID $gameId has already started or ended")
            throw ValidationException("Cannot enroll in a game that has already started or ended")
        }

        // Validate game is not full
        if (gameDetails.enrolledPlayers >= gameDetails.teamSize) {
            log.error("Cannot enroll player: game with ID $gameId is already full")
            throw GameFullException(gameId)
        }
    }
}
