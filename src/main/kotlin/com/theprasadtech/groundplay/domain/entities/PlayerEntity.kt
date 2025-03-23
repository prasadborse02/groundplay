package com.theprasadtech.groundplay.domain.entities

import jakarta.persistence.*

@Entity
@Table(name = "players")
data class PlayerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, unique = true)
    val phone: String
)
