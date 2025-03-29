package com.theprasadtech.groundplay.domain.dto

data class GameMembersDto(
    val gameDtoId: GameDto,
    val playerDtoId: PlayerDto,
    val playingStatus: Boolean,
)
