package com.theprasadtech.groundplay.repositories

import com.theprasadtech.groundplay.domain.entities.GameMemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface GameMemberRepository : JpaRepository<GameMemberEntity, Long?> {
    fun existsByGameIdAndPlayerId(
        gameId: Long,
        playerId: Long,
    ): Boolean

    fun findByGameId(gameId: Long): List<GameMemberEntity>
}
