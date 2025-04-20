package com.theprasadtech.groundplay.services

interface CustomEncryptionService {
    fun encrypt(
        password: String,
        salt: String,
    ): String

    fun decrypt(
        encryptedPassword: String,
        salt: String,
    ): String

    fun generateSalt(): String
}
