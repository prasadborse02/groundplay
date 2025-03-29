package com.theprasadtech.groundplay.domain.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.theprasadtech.groundplay.domain.entities.Sport
import jakarta.validation.constraints.Min
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

data class GameDto(
    var id: Long?,
    @field:NotNull
    var sport: Sport,
    var location: String?,
    @field:NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var startTime: LocalDateTime,
    @field:NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var endTime: LocalDateTime,
    var description: String?,
    @field:NotNull
    @field:Min(2)
    var teamSize: Int,
    @field:NotNull
    var status: Boolean,
    @field:NotNull
    var organizer: Long,
    @field:NotNull
    var coordinates: CoordinatesDto,
)

data class CoordinatesDto(
    val x: Double,
    val y: Double,
)
