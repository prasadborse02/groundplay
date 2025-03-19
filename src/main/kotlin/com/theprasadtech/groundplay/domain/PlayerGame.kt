package com.theprasadtech.groundplay.domain

import jakarta.persistence.*

@Entity
@Table(name = "player_game")
data class PlayerGame(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var gameId: Long,

    @Column(nullable = false)
    var playerId: Long,

    @Column(nullable = false)
    val playingStatus: Boolean
)
