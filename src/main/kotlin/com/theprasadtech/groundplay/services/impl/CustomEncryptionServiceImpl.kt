package com.theprasadtech.groundplay.services.impl

import com.theprasadtech.groundplay.services.CustomEncryptionService
import com.theprasadtech.groundplay.utils.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Implementation of CustomEncryptionService that uses AES encryption with CBC mode.
 * This implementation:
 * - Uses PBKDF2 to derive encryption keys from the secret and salt
 * - Employs AES/CBC/PKCS5Padding for the encryption algorithm
 * - Stores the initialization vector (IV) with the encrypted data
 */
@Service
class CustomEncryptionServiceImpl : CustomEncryptionService {
    private val log = logger()

    @Value("\${encryption.secret}")
    private lateinit var encryptionSecret: String

    private val algorithm = "AES/CBC/PKCS5Padding"
    private val keyAlgorithm = "PBKDF2WithHmacSHA256"
    private val keyLength = 256
    private val iterations = 65536

    override fun encrypt(
        password: String,
        salt: String,
    ): String {
        try {
            log.debug("Encrypting password with salt")

            // Generate encryption key from secret and salt
            val key = generateKey(encryptionSecret, salt)

            // Initialize cipher for encryption
            val cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.ENCRYPT_MODE, key)

            // Get the IV and encrypt the password
            val iv = cipher.parameters.getParameterSpec(IvParameterSpec::class.java).iv
            val encryptedBytes = cipher.doFinal(password.toByteArray())

            // Combine IV and encrypted bytes and encode to Base64
            val combined = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)

            return Base64.getEncoder().encodeToString(combined)
        } catch (e: Exception) {
            log.error("Error encrypting password", e)
            throw RuntimeException("Error during password encryption", e)
        }
    }

    override fun decrypt(
        encryptedPassword: String,
        salt: String,
    ): String {
        try {
            log.debug("Decrypting password with salt")

            // Generate decryption key from secret and salt
            val key = generateKey(encryptionSecret, salt)

            // Decode the Base64 string to get combined IV and encrypted data
            val combined = Base64.getDecoder().decode(encryptedPassword)

            // Split the IV and encrypted data
            val iv = ByteArray(16) // AES block size is 16 bytes
            val encryptedBytes = ByteArray(combined.size - iv.size)
            System.arraycopy(combined, 0, iv, 0, iv.size)
            System.arraycopy(combined, iv.size, encryptedBytes, 0, encryptedBytes.size)

            // Initialize cipher for decryption
            val cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))

            // Decrypt and return the result
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes)
        } catch (e: Exception) {
            log.error("Error decrypting password", e)
            throw RuntimeException("Error during password decryption", e)
        }
    }

    override fun generateSalt(): String {
        try {
            log.debug("Generating new salt")

            // Generate random salt
            val random = SecureRandom()
            val salt = ByteArray(16)
            random.nextBytes(salt)

            return Base64.getEncoder().encodeToString(salt)
        } catch (e: Exception) {
            log.error("Error generating salt", e)
            throw RuntimeException("Error generating salt", e)
        }
    }

    /**
     * Generates a SecretKey using PBKDF2 key derivation with the provided secret and salt.
     */
    private fun generateKey(
        secret: String,
        salt: String,
    ): SecretKey {
        val factory = SecretKeyFactory.getInstance(keyAlgorithm)
        val saltBytes = Base64.getDecoder().decode(salt)
        val spec = PBEKeySpec(secret.toCharArray(), saltBytes, iterations, keyLength)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }
}
