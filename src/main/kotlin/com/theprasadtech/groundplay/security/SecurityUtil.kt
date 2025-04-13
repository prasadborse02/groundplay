package com.theprasadtech.groundplay.security

import com.theprasadtech.groundplay.utils.logger
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/**
 * Utility class for security-related operations.
 * Provides methods to access the current authenticated user and perform authorization checks.
 * Used directly in code and as a SpEL evaluator in @PreAuthorize annotations.
 */
@Component("securityUtil")
class SecurityUtil(
    private val gameAuthorizationService: GameAuthorizationService,
) {
    private val log = logger()

    /**
     * Gets the ID of the currently authenticated player.
     * @return The player ID, or null if not authenticated
     */
    fun getCurrentPlayerId(): Long? {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication != null && authentication.isAuthenticated) {
            try {
                return authentication.principal as? Long
            } catch (e: Exception) {
                log.error("Error getting current player ID", e)
            }
        }

        return null
    }

    /**
     * Checks if a player is authenticated.
     * Usage in SpEL: @PreAuthorize("@securityUtil.isAuthenticated()")
     * @return true if authenticated, false otherwise
     */
    fun isAuthenticated(): Boolean {
        val result = getCurrentPlayerId() != null
        log.debug("Authentication check result: $result")
        return result
    }

    /**
     * Checks if the current authenticated player is the same as the specified player ID.
     * Usage in SpEL: @PreAuthorize("@securityUtil.isCurrentPlayer(#playerId)")
     * @param playerId The player ID to check against
     * @return true if the current player matches the specified ID, false otherwise
     */
    fun isCurrentPlayer(playerId: Long): Boolean {
        log.debug("Evaluating if current user is player: $playerId")
        val currentPlayerId = getCurrentPlayerId()
        val result = currentPlayerId != null && currentPlayerId == playerId
        log.debug("Current player check result: $result (for player: $playerId)")
        return result
    }

    /**
     * Checks if the current user is the organizer of the specified game.
     * Usage in SpEL: @PreAuthorize("@securityUtil.isGameOrganizer(#gameId)")
     * @param gameId The ID of the game to check
     * @return true if the current user is the organizer, false otherwise
     */
    fun isGameOrganizer(gameId: Long): Boolean {
        log.debug("Evaluating if current user is game organizer for game: $gameId")
        val currentPlayerId = getCurrentPlayerId() ?: return false

        val result = gameAuthorizationService.isGameOrganizer(gameId, currentPlayerId)
        log.debug("Game organizer check result: $result (player: $currentPlayerId, game: $gameId)")

        return result
    }
}
