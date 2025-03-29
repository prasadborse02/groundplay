package com.theprasadtech.groundplay.domain.entities

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class GameMemberId(
    val gameId: Long,
    val playerId: Long,
) : Serializable
