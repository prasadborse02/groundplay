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

    @Query(
        """
        SELECT *
        FROM games g
        WHERE ST_DWithin(
            g.coordinates, 
            ST_SetSRID(ST_MakePoint(:lat, :lon), 4326), 
            :radius
        ) AND g.status = true
        ORDER BY ST_Distance(
            g.coordinates, 
            ST_SetSRID(ST_MakePoint(:lat, :lon), 4326)
        )
        """,
        nativeQuery = true,
    )
    fun findGamesWithinRadius(
        @Param("lat") lat: Double,
        @Param("lon") lon: Double,
        @Param("radius") radius: Double,
    ): List<GameEntity>
}
