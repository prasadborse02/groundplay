package com.theprasadtech.groundplay.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "game_members")
data class GameMemberEntity(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long?,
    @Column(nullable = false)
    val gameId: Long,
    @Column(nullable = false)
    val playerId: Long,
    val status: Boolean,
)
