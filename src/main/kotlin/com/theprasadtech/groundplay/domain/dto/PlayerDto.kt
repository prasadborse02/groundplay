package com.theprasadtech.groundplay.domain.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.jetbrains.annotations.NotNull

data class PlayerDto(
    val id: Long?,
    @field:NotNull
    val name: String,
    @field:NotNull
    @field:Pattern(
        regexp = "^\\+?[0-9]{7,15}$",
        message = "Invalid phone number format",
    )
    val phoneNumber: String,
    @field:Email(message = "Invalid email format")
    val email: String? = null,
)
