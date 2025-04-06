package com.theprasadtech.groundplay.services.impl

import com.theprasadtech.groundplay.domain.GameUpdateRequest
import com.theprasadtech.groundplay.domain.dto.CoordinatesDto
import com.theprasadtech.groundplay.domain.entities.GameEntity
import com.theprasadtech.groundplay.exceptions.GameConflictException
import com.theprasadtech.groundplay.exceptions.PlayerNotAvailableException
import com.theprasadtech.groundplay.exceptions.ResourceNotFoundException
import com.theprasadtech.groundplay.exceptions.UnauthorizedOperationException
import com.theprasadtech.groundplay.exceptions.ValidationException
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
    private val log = logger()

    // Constants
    companion object {
        private const val MAX_GAME_DURATION_HOURS = 24
        private const val MAX_TEAM_SIZE = 100
        private const val MAX_SEARCH_RADIUS_KM = 100
    }

    override fun create(gameEntity: GameEntity): GameEntity {
        log.info("Creating new game: sport=${gameEntity.sport}, location=${gameEntity.location}, organizer=${gameEntity.organizer}")

        if (gameEntity.id != null) {
            log.error("Cannot create game with predefined ID: ${gameEntity.id}")
            throw ValidationException("New game cannot have a predefined ID")
        }

        if (!playerRepository.existsById(gameEntity.organizer)) {
            log.error("Cannot create game: organizer with ID ${gameEntity.organizer} not found")
            throw ResourceNotFoundException("Player", gameEntity.organizer)
        }

        validateGameTime(gameEntity.startTime, gameEntity.endTime)
        validateTeamSize(gameEntity.teamSize)

        val conflicts = gameRepository.findConflictingGames(gameEntity.startTime, gameEntity.endTime, gameEntity.coordinates)

        if (conflicts.isNotEmpty()) {
            log.error("Cannot create game: location conflict with existing games. conflictCount=${conflicts.size}")
            throw GameConflictException("A game already exists in this location at the given time.")
        }

        val savedGame = gameRepository.save(gameEntity)
        log.info("Successfully created game with ID: ${savedGame.id}")
        return savedGame
    }

    override fun getById(id: Long): GameEntity? {
        log.debug("Fetching game with ID: $id")
        val game = gameRepository.findByIdOrNull(id)
        if (game == null) {
            log.debug("Game not found with ID: $id")
        } else {
            log.debug("Found game: id=$id, sport=${game.sport}, status=${game.status}")
        }
        return game
    }

    override fun getGamesNearby(
        lat: Double,
        lon: Double,
        radiusInKm: Double,
    ): List<GameEntity> {
        if (radiusInKm <= 0 || radiusInKm > MAX_SEARCH_RADIUS_KM) {
            log.error("Invalid radius for nearby games search: $radiusInKm km")
            throw ValidationException("Radius must be between 1 and $MAX_SEARCH_RADIUS_KM km")
        }

        log.info("Searching for games near lat=$lat, lon=$lon with radius=${radiusInKm}km")
        val games = gameRepository.findGamesWithinRadius(lat, lon, radiusInKm * 1000)
        log.info("Found ${games.size} games within ${radiusInKm}km radius")

        if (games.isEmpty()) {
            log.debug("No games found in the specified area")
        } else {
            games.forEach { game ->
                log.debug("Found game in radius: id=${game.id}, sport=${game.sport}, location=${game.location}")
            }
        }

        return games
    }

    override fun getByOrganizerId(id: Long): List<GameEntity> {
        playerRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Player", id)

        log.info("Searching for games with organizerId: $id")
        val games = gameRepository.findGamesByOrganizer(id)
        log.info("Found ${games.size} games for organizerId $id")

        if(games.isEmpty()) {
            log.debug("No games found for the organizerId")
        } else {
            games.forEach { game ->
                log.debug("Found game for organizerId $id: id=${game.id}, sport=${game.sport}")
            }
        }

        return games;
    }

    @Transactional
    override fun updateGame(
        id: Long,
        gameUpdateRequest: GameUpdateRequest,
    ): GameEntity {
        log.info("Updating game with ID: $id")

        val existingGame =
            gameRepository.findByIdOrNull(id)
                ?: throw ResourceNotFoundException("Game", id)

        // Verify organizer
        if (existingGame.organizer != gameUpdateRequest.organizer) {
            log.error("Update rejected: Requester (${gameUpdateRequest.organizer}) is not the game organizer (${existingGame.organizer})")
            throw UnauthorizedOperationException("Only the game organizer can update the game")
        }

        val finalStartTime = gameUpdateRequest.startTime ?: existingGame.startTime
        val finalEndTime = gameUpdateRequest.endTime ?: existingGame.endTime

        validateGameTime(finalStartTime, finalEndTime)

        gameUpdateRequest.teamSize?.let {
            validateTeamSize(it)
        }

        val conflictingPlayerIds = gameRepository.findConflictingPlayerIds(id, finalStartTime, finalEndTime)
        if (conflictingPlayerIds.isNotEmpty()) {
            log.error("Cannot update game: conflict with ${conflictingPlayerIds.size} players' schedules")
            throw PlayerNotAvailableException(conflictingPlayerIds)
        }

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

        val savedGame = gameRepository.save(updatedGame)
        log.info("Successfully updated game with ID: $id")
        return savedGame
    }

    override fun convertToPoint(coordinatesDto: CoordinatesDto): Point =
        GeometryFactory().createPoint(Coordinate(coordinatesDto.lat, coordinatesDto.lon))

    // Helper validation methods
    private fun validateGameTime(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
    ) {
        if (!startTime.isBefore(endTime)) {
            log.error("Invalid game time: start time ($startTime) is not before end time ($endTime)")
            throw ValidationException("Start time must be before end time")
        }

        val duration = Duration.between(startTime, endTime).toHours()
        if (duration > MAX_GAME_DURATION_HOURS) {
            log.error("Invalid game duration: $duration hours exceeds maximum of $MAX_GAME_DURATION_HOURS hours")
            throw ValidationException("Game duration cannot exceed $MAX_GAME_DURATION_HOURS hours")
        }

        if (!startTime.isAfter(LocalDateTime.now())) {
            log.error("Invalid game time: start time ($startTime) is not in the future")
            throw ValidationException("Game must be scheduled for the future")
        }
    }

    private fun validateTeamSize(teamSize: Int) {
        if (teamSize > MAX_TEAM_SIZE) {
            log.error("Invalid team size: $teamSize exceeds maximum of $MAX_TEAM_SIZE")
            throw ValidationException("Team size cannot exceed $MAX_TEAM_SIZE players")
        }
    }
}
