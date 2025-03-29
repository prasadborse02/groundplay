package com.theprasadtech.groundplay.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table

@Entity
@Table(name = "game_members")
data class GameMembersEntity(
    @EmbeddedId
    val id: GameMemberId,
    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id")
    val gameEntityId: GameEntity,
    @ManyToOne
    @MapsId("playerId")
    @JoinColumn(name = "player_id")
    val playerEntityId: PlayerEntity,
    @Column(nullable = false)
    val playingStatus: Boolean,
)
