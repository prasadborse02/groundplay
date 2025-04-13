package com.theprasadtech.groundplay.repositories

import com.theprasadtech.groundplay.domain.entities.GameMemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface GameMemberRepository : JpaRepository<GameMemberEntity, Long?> {
    fun existsByGameIdAndPlayerId(
        gameId: Long,
        playerId: Long,
    ): Boolean

    @Query(
        """
        SELECT COUNT(*) = 0
        FROM games g
        JOIN game_members gm ON g.id = gm.game_id
        WHERE gm.player_id = :playerId
        AND gm.status = true
        AND (
            (:startTime > g.start_time AND :startTime < g.end_time) 
            OR 
            (:endTime > g.start_time AND :endTime < g.end_time)
            OR 
            (g.start_time >= :startTime AND g.start_time < :endTime)
            OR 
            (g.end_time > :startTime AND g.end_time <= :endTime)
            OR
            (:startTime < g.start_time AND :endTime > g.end_time)
        )
        """,
        nativeQuery = true,
    )
    fun isPlayerAvailable(
        @Param("startTime") newGameStartTime: LocalDateTime,
        @Param("endTime") newGameEndTime: LocalDateTime,
        @Param("playerId") playerId: Long,
    ): Boolean

    fun findByGameIdAndStatus(
        gameId: Long,
        status: Boolean,
    ): List<GameMemberEntity>

    fun findByGameIdAndPlayerId(
        gameId: Long,
        playerId: Long,
    ): GameMemberEntity

    fun findByPlayerIdAndStatus(
        playerId: Long,
        status: Boolean,
    ): List<GameMemberEntity>

    fun findByPlayerId(playerId: Long): List<GameMemberEntity>
}
