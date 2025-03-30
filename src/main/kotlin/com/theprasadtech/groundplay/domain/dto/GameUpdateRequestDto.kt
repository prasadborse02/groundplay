package com.theprasadtech.groundplay.domain.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.theprasadtech.groundplay.domain.entities.Sport
import jakarta.validation.constraints.Min
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

data class GameUpdateRequestDto(
    val sport: Sport?,
    val location: String?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val startTime: LocalDateTime?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val endTime: LocalDateTime?,
    val description: String?,
    @field:Min(2)
    val teamSize: Int?,
    val status: Boolean?,
    @field:NotNull
    val organizer: Long,
    val coordinates: CoordinatesDto?,
)
