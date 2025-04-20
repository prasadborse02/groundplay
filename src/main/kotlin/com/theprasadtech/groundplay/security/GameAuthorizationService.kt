package com.theprasadtech.groundplay.security

import com.theprasadtech.groundplay.repositories.GameRepository
import com.theprasadtech.groundplay.utils.logger
import org.springframework.stereotype.Service

@Service
class GameAuthorizationService(
    private val gameRepository: GameRepository,
) {
    private val log = logger()

    fun isGameOrganizer(
        gameId: Long,
        playerId: Long,
    ): Boolean {
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
}
