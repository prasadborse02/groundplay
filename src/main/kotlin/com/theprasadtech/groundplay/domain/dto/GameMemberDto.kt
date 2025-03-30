package com.theprasadtech.groundplay.domain.dto

import org.jetbrains.annotations.NotNull

data class GameMemberDto(
    val id: Long?,
    @field:NotNull
    val gameId: Long,
    @field:NotNull
    val playerId: Long,
    @field:NotNull
    val status: Boolean = true,
)
