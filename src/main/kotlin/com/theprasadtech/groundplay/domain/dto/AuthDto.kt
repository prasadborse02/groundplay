package com.theprasadtech.groundplay.domain.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegisterRequestDto(
    @field:NotBlank(message = "Name is required")
    val name: String,
    @field:NotBlank(message = "Phone number is required")
    @field:Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number format")
    val phoneNumber: String,
    @field:Email(message = "Invalid email format")
    val email: String? = null,
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    val password: String,
)

data class LoginRequestDto(
    @field:NotBlank(message = "Phone number is required")
    @field:Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number format")
    val phoneNumber: String,
    @field:NotBlank(message = "Password is required")
    val password: String,
)

data class AuthResponseDto(
    val token: String,
    val playerId: Long,
)
