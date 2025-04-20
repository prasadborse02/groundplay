package com.theprasadtech.groundplay.services.impl

import com.theprasadtech.groundplay.domain.entities.PlayerEntity
import com.theprasadtech.groundplay.exceptions.ResourceAlreadyExistsException
import com.theprasadtech.groundplay.repositories.PlayerRepository
import com.theprasadtech.groundplay.services.AuthenticationService
import com.theprasadtech.groundplay.services.CustomEncryptionService
import com.theprasadtech.groundplay.services.JwtTokenService
import com.theprasadtech.groundplay.utils.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthenticationServiceImpl(
    private val playerRepository: PlayerRepository,
    private val encryptionService: CustomEncryptionService,
    private val jwtTokenService: JwtTokenService,
) : AuthenticationService {
    private val log = logger()

    @Transactional
    override fun registerPlayer(
        name: String,
        phoneNumber: String,
        email: String?,
        password: String,
    ): PlayerEntity {
        log.info("Registering new player with name: $name, phone: $phoneNumber")

        // Validate that phone number is not already registered
        if (playerRepository.existsByPhoneNumber(phoneNumber)) {
            log.warn("Registration failed: Phone number $phoneNumber already registered")
            throw ResourceAlreadyExistsException("Player", "phoneNumber", phoneNumber)
        }

        // Generate salt and encrypt password
        val salt = encryptionService.generateSalt()
        val encryptedPassword = encryptionService.encrypt(password, salt)

        // Create and save the player
        val player =
            PlayerEntity(
                name = name,
                phoneNumber = phoneNumber,
                email = email,
                password = encryptedPassword,
                salt = salt,
            )

        val savedPlayer = playerRepository.save(player)
        log.info("Successfully registered player with ID: ${savedPlayer.id}")

        return savedPlayer
    }

    override fun authenticatePlayer(
        phoneNumber: String,
        password: String,
    ): String {
        log.info("Authenticating player with phone number: $phoneNumber")

        // Find player by phone number
        val player =
            playerRepository.findByPhoneNumber(phoneNumber)
                ?: run {
                    log.warn("Authentication failed: No player found with phone number $phoneNumber")
                    throw IllegalStateException("Invalid credentials")
                }

        // Decrypt stored password and compare with provided password
        try {
            val decryptedPassword = encryptionService.decrypt(player.password, player.salt)

            if (decryptedPassword != password) {
                log.warn("Authentication failed: Invalid password for player ID ${player.id}")
                throw IllegalStateException("Invalid credentials")
            }

            // Generate JWT token
            val token = jwtTokenService.generateToken(player.id!!, player.phoneNumber)
            log.info("Successfully authenticated player ID: ${player.id}")

            return token
        } catch (e: Exception) {
            log.error("Error during authentication", e)
            throw IllegalStateException("Authentication failed")
        }
    }
}
