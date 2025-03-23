package com.theprasadtech.groundplay.domain.dto

import com.theprasadtech.groundplay.domain.entities.Sport
import org.locationtech.jts.geom.Point
import java.time.LocalDateTime

data class GameDto(
    var id: Long?,
    var sport: Sport,
    var location: String?,
    var startTime: LocalDateTime,
    var endTime: LocalDateTime,
    var description: String?,
    var status: Boolean,
    var organizer: PlayerDto,
    var coordinates: Point
)
