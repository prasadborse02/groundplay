package com.theprasadtech.groundplay.controllers

import com.theprasadtech.groundplay.domain.dto.AuthResponseDto
import com.theprasadtech.groundplay.domain.dto.LoginRequestDto
import com.theprasadtech.groundplay.domain.dto.RegisterRequestDto
import com.theprasadtech.groundplay.services.AuthenticationService
import com.theprasadtech.groundplay.services.JwtTokenService
import com.theprasadtech.groundplay.utils.logger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationService: AuthenticationService,
    private val jwtTokenService: JwtTokenService,
) {
    private val log = logger()

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequestDto,
    ): ResponseEntity<AuthResponseDto> {
        try {
            log.info("Received registration request for ${request.phoneNumber}")

            val player =
                authenticationService.registerPlayer(
                    name = request.name,
                    phoneNumber = request.phoneNumber,
                    email = request.email,
                    password = request.password,
                )

            // Generate token for automatic login after registration
            val token =
                authenticationService.authenticatePlayer(
                    phoneNumber = request.phoneNumber,
                    password = request.password,
                )

            return ResponseEntity.status(HttpStatus.CREATED).body(
                AuthResponseDto(
                    token = token,
                    playerId = player.id!!,
                ),
            )
        } catch (e: IllegalStateException) {
            log.warn("Registration failed: ${e.message}")
            throw e
        }
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequestDto,
    ): ResponseEntity<AuthResponseDto> {
        try {
            log.info("Received login request for ${request.phoneNumber}")

            val token =
                authenticationService.authenticatePlayer(
                    phoneNumber = request.phoneNumber,
                    password = request.password,
                )

            // Extract player ID from token
            val playerId = jwtTokenService.getPlayerIdFromToken(token)

            return ResponseEntity.ok(
                AuthResponseDto(
                    token = token,
                    playerId = playerId,
                ),
            )
        } catch (e: IllegalStateException) {
            log.warn("Login failed: ${e.message}")
            throw e
        }
    }
}
