package com.theprasadtech.groundplay.services

import com.theprasadtech.groundplay.domain.dto.CoordinatesDto
import com.theprasadtech.groundplay.domain.entities.GameEntity
import org.locationtech.jts.geom.Point

interface GameService {
    fun save(gameEntity: GameEntity): GameEntity

    fun convertToPoint(coordinatesDto: CoordinatesDto): Point
}