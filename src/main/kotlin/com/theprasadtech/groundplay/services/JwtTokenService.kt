package com.theprasadtech.groundplay.services

/**
 * Service for handling JWT token operations.
 * Provides methods for generating, validating, and extracting data from JWT tokens.
 */
interface JwtTokenService {
    /**
     * Generates a JWT token for the given player.
     *
     * @param playerId The ID of the player
     * @param phoneNumber The phone number of the player
     * @return A signed JWT token string
     */
    fun generateToken(
        playerId: Long,
        phoneNumber: String,
    ): String

    /**
     * Validates a JWT token.
     *
     * @param token The JWT token to validate
     * @return True if the token is valid, false otherwise
     */
    fun validateToken(token: String): Boolean

    /**
     * Extracts the player ID from a JWT token.
     *
     * @param token The JWT token
     * @return The player ID
     * @throws IllegalArgumentException if the player ID cannot be extracted
     */
    fun getPlayerIdFromToken(token: String): Long

    /**
     * Extracts the phone number from a JWT token.
     *
     * @param token The JWT token
     * @return The phone number
     * @throws IllegalArgumentException if the phone number cannot be extracted
     */
    fun getPhoneNumberFromToken(token: String): String

    /**
     * Checks if a JWT token is expired.
     *
     * @param token The JWT token
     * @return True if the token is expired, false otherwise
     */
    fun isTokenExpired(token: String): Boolean
}
