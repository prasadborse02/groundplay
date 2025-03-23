package com.theprasadtech.groundplay.domain.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.theprasadtech.groundplay.domain.entities.Sport
import java.time.LocalDateTime

data class GameDto(
    var id: Long?,
    var sport: Sport,
    var location: String?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var startTime: LocalDateTime,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var endTime: LocalDateTime,
    var description: String?,
    var status: Boolean,
    var organizer: Long,
    var coordinates: CoordinatesDto
)

data class CoordinatesDto(
    val x: Double,
    val y: Double
)