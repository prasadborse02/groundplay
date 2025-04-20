package com.theprasadtech.groundplay.services

import com.theprasadtech.groundplay.domain.entities.PlayerEntity

interface AuthenticationService {
    fun registerPlayer(
        name: String,
        phoneNumber: String,
        email: String?,
        password: String,
    ): PlayerEntity

    fun authenticatePlayer(
        phoneNumber: String,
        password: String,
    ): String
}
