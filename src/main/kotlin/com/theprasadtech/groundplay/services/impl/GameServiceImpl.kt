package com.theprasadtech.groundplay.services.impl

import com.theprasadtech.groundplay.domain.dto.CoordinatesDto
import com.theprasadtech.groundplay.domain.entities.GameEntity
import com.theprasadtech.groundplay.repositories.GameRepository
import com.theprasadtech.groundplay.services.GameService
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.springframework.stereotype.Service

@Service
class GameServiceImpl(
    private val gameRepository: GameRepository,
) : GameService {
    override fun save(gameEntity: GameEntity): GameEntity = gameRepository.save(gameEntity)

    override fun getById(id: Long): GameEntity =
        gameRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Game not found with id: $id") }

    override fun convertToPoint(coordinatesDto: CoordinatesDto): Point =
        GeometryFactory().createPoint(Coordinate(coordinatesDto.x, coordinatesDto.y))
}
