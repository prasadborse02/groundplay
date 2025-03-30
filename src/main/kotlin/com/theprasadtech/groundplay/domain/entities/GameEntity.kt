package com.theprasadtech.groundplay.domain.entities

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.theprasadtech.groundplay.utils.PointDeserializer
import com.theprasadtech.groundplay.utils.PointSerializer
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.locationtech.jts.geom.Point
import java.time.LocalDateTime

@Entity
@Table(name = "games")
data class GameEntity(
    @Id
    @GeneratedValue(strategy = IDENTITY)
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
    val teamSize: Int,
    @Column(nullable = false)
    val enrolledPlayers: Int = 0,
    @Column(nullable = false)
    val status: Boolean,
    @Column(nullable = false)
    val organizer: Long,
    @JsonSerialize(using = PointSerializer::class)
    @JsonDeserialize(using = PointDeserializer::class)
    @Column(columnDefinition = "GEOGRAPHY(Point, 4326)", nullable = false)
    val coordinates: Point,
)
