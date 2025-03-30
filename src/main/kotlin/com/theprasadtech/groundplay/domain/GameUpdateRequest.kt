package com.theprasadtech.groundplay.domain

import com.fasterxml.jackson.annotation.JsonFormat
import com.theprasadtech.groundplay.domain.dto.CoordinatesDto
import com.theprasadtech.groundplay.domain.entities.Sport
import jakarta.validation.constraints.Min
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

data class GameUpdateRequest(
    var id: Long?,
    var sport: Sport?,
    var location: String?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var startTime: LocalDateTime?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var endTime: LocalDateTime?,
    var description: String?,
    @field:Min(2)
    var teamSize: Int?,
    var status: Boolean?,
    @field:NotNull
    var organizer: Long,
    var coordinates: CoordinatesDto?,
)
