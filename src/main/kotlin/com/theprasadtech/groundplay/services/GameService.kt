package com.theprasadtech.groundplay.services

import com.theprasadtech.groundplay.domain.GameUpdateRequest
import com.theprasadtech.groundplay.domain.dto.CoordinatesDto
import com.theprasadtech.groundplay.domain.entities.GameEntity
import org.locationtech.jts.geom.Point

interface GameService {
    fun create(gameEntity: GameEntity): GameEntity

    fun getById(id: Long): GameEntity?

    fun getGamesNearby(
        lat: Double,
        lon: Double,
        radiusInKm: Double,
    ): List<GameEntity>

    fun updateGame(
        id: Long,
        gameUpdateRequest: GameUpdateRequest,
    ): GameEntity

    fun convertToPoint(coordinatesDto: CoordinatesDto): Point
}
