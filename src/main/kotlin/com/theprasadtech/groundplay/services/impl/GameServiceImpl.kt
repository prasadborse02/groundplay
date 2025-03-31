package com.theprasadtech.groundplay.services.impl

import com.theprasadtech.groundplay.domain.GameUpdateRequest
import com.theprasadtech.groundplay.domain.dto.CoordinatesDto
import com.theprasadtech.groundplay.domain.entities.GameEntity
import com.theprasadtech.groundplay.repositories.GameRepository
import com.theprasadtech.groundplay.repositories.PlayerRepository
import com.theprasadtech.groundplay.services.GameService
import com.theprasadtech.groundplay.utils.logger
import jakarta.transaction.Transactional
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class GameServiceImpl(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
) : GameService {
    private val logger = logger()

    override fun create(gameEntity: GameEntity): GameEntity {
        require(null == gameEntity.id)
        check(playerRepository.existsById(gameEntity.organizer))
        check(gameEntity.startTime.isBefore(gameEntity.endTime)) { "Start time must be before end time" }
        check(Duration.between(gameEntity.startTime, gameEntity.endTime).toHours() <= 24) { "Game duration cannot exceed 24 hours" }
        check(gameEntity.startTime.isAfter(LocalDateTime.now())) { "Game must be scheduled for the future" }
        val conflicts = gameRepository.findConflictingGames(gameEntity.startTime, gameEntity.endTime, gameEntity.coordinates)

        if (conflicts.isNotEmpty()) {
            throw IllegalArgumentException("A game already exists in this location at the given time.")
        }

        return gameRepository.save(gameEntity)
    }

    override fun getById(id: Long): GameEntity? = gameRepository.findByIdOrNull(id)

    override fun getGamesNearby(
        lat: Double,
        lon: Double,
        radiusInKm: Double,
    ): List<GameEntity> {
        require(0 < radiusInKm && radiusInKm <= 100)
        logger.info("Searching for games near lat: $lat, lon: $lon with radius: ${radiusInKm}km")
        val games = gameRepository.findGamesWithinRadius(lat, lon, radiusInKm * 1000)
        logger.info("Found ${games.size} games within radius")
        return games
    }

    @Transactional
    override fun updateGame(
        id: Long,
        gameUpdateRequest: GameUpdateRequest,
    ): GameEntity {
        val existingGame = gameRepository.findByIdOrNull(id)
        checkNotNull(existingGame)
        check(existingGame.organizer == gameUpdateRequest.organizer)

        val finalStartTime = gameUpdateRequest.startTime ?: existingGame.startTime
        val finalEndTime = gameUpdateRequest.endTime ?: existingGame.endTime

        check(finalStartTime.isBefore(finalEndTime)) { "Start time must be before end time" }
        check(Duration.between(finalStartTime, finalEndTime).toHours() <= 24) { "Game duration cannot exceed 24 hours" }
        check(finalStartTime.isAfter(LocalDateTime.now())) { "Game must be scheduled for the future" }

        gameUpdateRequest.teamSize?.let {
            check(it <= 50) { "Team size cannot exceed 50 players" }
        }

        // TODO: Deregister player if player is not available within timeDuration

        val updatedGame =
            existingGame.copy(
                id = id,
                sport = gameUpdateRequest.sport ?: existingGame.sport,
                location = gameUpdateRequest.location ?: existingGame.location,
                startTime = finalStartTime,
                endTime = finalEndTime,
                description = gameUpdateRequest.description ?: existingGame.description,
                teamSize = gameUpdateRequest.teamSize ?: existingGame.teamSize,
                status = gameUpdateRequest.status ?: existingGame.status,
                coordinates = gameUpdateRequest.coordinates?.let { convertToPoint(it) } ?: existingGame.coordinates,
            )

        return gameRepository.save(updatedGame)
    }

    override fun convertToPoint(coordinatesDto: CoordinatesDto): Point =
        GeometryFactory().createPoint(Coordinate(coordinatesDto.x, coordinatesDto.y))
}
