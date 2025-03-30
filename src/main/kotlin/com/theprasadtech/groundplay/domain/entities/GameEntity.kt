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
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
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
    var sport: Sport,
    var location: String? = null,
    @Column(nullable = false)
    var startTime: LocalDateTime,
    @Column(nullable = false)
    var endTime: LocalDateTime,
    @Column(columnDefinition = "TEXT")
    var description: String? = null,
    @Column(nullable = false)
    var teamSize: Int,
    @Column(nullable = false)
    var status: Boolean,
    @Column(nullable = false)
    val organizer: Long,
    @JsonSerialize(using = PointSerializer::class)
    @JsonDeserialize(using = PointDeserializer::class)
    @Column(columnDefinition = "GEOGRAPHY(Point, 4326)", nullable = false)
    var coordinates: Point,
)
