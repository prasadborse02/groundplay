package com.theprasadtech.groundplay

import com.theprasadtech.groundplay.domain.GameUpdateRequest
import com.theprasadtech.groundplay.domain.PlayerUpdateRequest
import com.theprasadtech.groundplay.domain.dto.CoordinatesDto
import com.theprasadtech.groundplay.domain.dto.GameDto
import com.theprasadtech.groundplay.domain.dto.GameMemberDto
import com.theprasadtech.groundplay.domain.dto.GameUpdateRequestDto
import com.theprasadtech.groundplay.domain.dto.PlayerDto
import com.theprasadtech.groundplay.domain.dto.PlayerUpdateRequestDto
import com.theprasadtech.groundplay.domain.entities.GameEntity
import com.theprasadtech.groundplay.domain.entities.GameMemberEntity
import com.theprasadtech.groundplay.domain.entities.PlayerEntity
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel

private val geometryFactory = GeometryFactory(PrecisionModel(), 4326)

fun GameEntity.toGameDto() =
    GameDto(
        id = this.id,
        sport = this.sport,
        location = this.location,
        startTime = this.startTime,
        endTime = this.endTime,
        description = this.description,
        teamSize = this.teamSize,
        enrolledPlayers = this.enrolledPlayers,
        status = this.status,
        organizer = this.organizer,
        coordinates =
            CoordinatesDto(
                lat = this.coordinates.y,
                lon = this.coordinates.x,
            ),
    )

fun GameDto.toGameEntity() =
    GameEntity(
        id = this.id,
        sport = this.sport,
        location = this.location,
        startTime = this.startTime,
        endTime = this.endTime,
        description = this.description,
        teamSize = this.teamSize,
        enrolledPlayers = this.enrolledPlayers,
        status = this.status,
        organizer = this.organizer,
        coordinates =
            geometryFactory.createPoint(
                Coordinate(this.coordinates.lon, this.coordinates.lat),
            ),
    )

fun GameMemberEntity.toGameMemberDto() =
    GameMemberDto(
        id = this.id,
        gameId = this.gameId,
        playerId = this.playerId,
        status = this.status,
    )

fun GameMemberDto.toGameMemberEntity() =
    GameMemberEntity(
        id = this.id,
        gameId = this.gameId,
        playerId = this.playerId,
        status = this.status,
    )

fun GameUpdateRequestDto.toGameUpdateRequest() =
    GameUpdateRequest(
        sport = this.sport,
        location = this.location,
        startTime = this.startTime,
        endTime = this.endTime,
        description = this.description,
        teamSize = this.teamSize,
        status = this.status,
        organizer = this.organizer,
        coordinates = this.coordinates?.let { CoordinatesDto(it.lat, it.lon) },
    )

fun PlayerEntity.toPlayerDto() =
    PlayerDto(
        id = this.id,
        name = this.name,
        phoneNumber = this.phoneNumber,
        email = this.email,
    )

fun PlayerUpdateRequestDto.toPlayerUpdateRequest() =
    PlayerUpdateRequest(
        name = this.name,
    )
