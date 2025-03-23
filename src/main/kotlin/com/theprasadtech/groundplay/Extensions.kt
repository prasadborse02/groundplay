package com.theprasadtech.groundplay

import com.theprasadtech.groundplay.domain.dto.GameDto
import com.theprasadtech.groundplay.domain.dto.PlayerDto
import com.theprasadtech.groundplay.domain.entities.GameEntity
import com.theprasadtech.groundplay.domain.entities.PlayerEntity
import com.theprasadtech.groundplay.domain.dto.CoordinatesDto
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel

private val geometryFactory = GeometryFactory(PrecisionModel(), 4326)

fun GameEntity.toGameDto() = GameDto(
    id = this.id,
    sport = this.sport,
    location = this.location,
    startTime = this.startTime,
    endTime = this.endTime,
    description = this.description,
    status = this.status,
    organizer = this.organizer,
    coordinates = CoordinatesDto(
        x = this.coordinates.x,
        y = this.coordinates.y
    )
)

fun GameDto.toGameEntity() = GameEntity(
    id = this.id,
    sport = this.sport,
    location = this.location,
    startTime = this.startTime,
    endTime = this.endTime,
    description = this.description,
    status = this.status,
    organizer = this.organizer,
    coordinates = geometryFactory.createPoint(
        Coordinate(this.coordinates.x, this.coordinates.y)
    )
)

fun PlayerEntity.toPlayerDto() = PlayerDto(
    id = this.id,
    name = this.name,
    phone = this.phone
)

fun PlayerDto.toPlayerEntity() = PlayerEntity(
    id = this.id,
    name = this.name,
    phone = this.phone
)