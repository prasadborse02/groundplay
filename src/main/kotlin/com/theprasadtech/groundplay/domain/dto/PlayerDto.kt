package com.theprasadtech.groundplay.domain.dto

import org.jetbrains.annotations.NotNull

data class PlayerDto(
    val id: Long?,
    @field:NotNull
    val name: String,
    // TODO: Add validation for phone number
    @field:NotNull
    val phone: String,
)
