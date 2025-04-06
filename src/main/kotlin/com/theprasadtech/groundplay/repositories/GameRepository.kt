package com.theprasadtech.groundplay.repositories

import com.theprasadtech.groundplay.domain.entities.GameEntity
import org.locationtech.jts.geom.Point
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface GameRepository : JpaRepository<GameEntity, Long?> {
    @Query(
        """
        SELECT * FROM games g
        WHERE g.coordinates = :coordinate
        AND g.status = true
        AND (
            (:newGameStartTime >= g.start_time AND :newGameStartTime < g.end_time) 
            OR (:newGameEndTime > g.start_time AND :newGameEndTime <= g.end_time) 
            OR (:newGameStartTime <= g.start_time AND :newGameEndTime >= g.end_time)
        )
        """,
        nativeQuery = true,
    )
    fun findConflictingGames(
        @Param("newGameStartTime") newGameStartTime: LocalDateTime,
        @Param("newGameEndTime") newGameEndTime: LocalDateTime,
        @Param("coordinate") coordinate: Point,
    ): List<GameEntity>

    fun findGameById(id: Long): GameEntity

    fun findGamesByOrganizer(organizer: Long): List<GameEntity>

    @Query(
        """
        SELECT *
        FROM games g
        WHERE ST_DWithin(
            g.coordinates, 
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326), 
            :radius
        ) 
        AND g.status = true
        AND g.start_time > CURRENT_TIMESTAMP
        ORDER BY ST_Distance(
            g.coordinates, 
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)
        )
        """,
        nativeQuery = true,
    )
    fun findGamesWithinRadius(
        @Param("lat") lat: Double,
        @Param("lon") lon: Double,
        @Param("radius") radius: Double,
    ): List<GameEntity>

    @Query(
        """
        SELECT gm.player_id
        FROM game_members gm
        JOIN games gA ON gm.game_id = gA.id
        JOIN games gB ON gm.player_id IN (
            SELECT player_id FROM game_members WHERE game_id = gB.id
        )
        WHERE gA.id = :gameId
        AND gB.id != :gameId
        AND gB.status = true
        AND gm.status = true
        AND (
            (:newStartTime > gB.start_time AND :newStartTime < gB.end_time) 
            OR 
            (:newEndTime > gB.start_time AND :newEndTime < gB.end_time)
            OR 
            (gB.start_time >= :newStartTime AND gB.start_time < :newEndTime)
            OR 
            (gB.end_time > :newStartTime AND gB.end_time <= :newEndTime)
            OR
            (:newStartTime < gB.start_time AND :newEndTime > gB.end_time)
        )
        """,
        nativeQuery = true,
    )
    fun findConflictingPlayerIds(
        @Param("gameId") gameId: Long,
        @Param("newStartTime") newStartTime: LocalDateTime,
        @Param("newEndTime") newEndTime: LocalDateTime,
    ): List<Long>
}
