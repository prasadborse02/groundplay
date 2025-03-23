package com.theprasadtech.groundplay.domain.entities

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.theprasadtech.groundplay.utils.PointDeserializer
import jakarta.persistence.*
import org.locationtech.jts.geom.Point
import java.time.LocalDateTime

@Entity
@Table(name = "games")
data class GameEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val sport: Sport,

    val location: String? = null,

    @Column(nullable = false)
    val startTime: LocalDateTime,

    @Column(nullable = false)
    val endTime: LocalDateTime,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(nullable = false)
    val status: Boolean,

    @Column(nullable = false)
    val organizer: Long,

    @JsonDeserialize(using = PointDeserializer::class)
    @Column(columnDefinition = "GEOGRAPHY(Point, 4326)", nullable = false)
    val coordinates: Point
)