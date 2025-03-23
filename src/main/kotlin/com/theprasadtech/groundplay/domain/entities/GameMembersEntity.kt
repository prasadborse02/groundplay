package com.theprasadtech.groundplay.domain.entities

import jakarta.persistence.*

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
    val playingStatus: Boolean
)
