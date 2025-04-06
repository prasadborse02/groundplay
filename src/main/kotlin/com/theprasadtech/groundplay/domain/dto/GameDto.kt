package com.theprasadtech.groundplay.domain.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.theprasadtech.groundplay.domain.entities.Sport
import jakarta.validation.constraints.Min
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

data class GameDto(
    val id: Long?,
    @field:NotNull
    val sport: Sport,
    val location: String?,
    @field:NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val startTime: LocalDateTime,
    @field:NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val endTime: LocalDateTime,
    val description: String?,
    @field:NotNull
    @field:Min(2)
    val teamSize: Int,
    val enrolledPlayers: Int,
    @field:NotNull
    val status: Boolean,
    @field:NotNull
    val organizer: Long,
    @field:NotNull
    val coordinates: CoordinatesDto,
)

data class CoordinatesDto(
    val lat: Double,
    val lon: Double,
)
