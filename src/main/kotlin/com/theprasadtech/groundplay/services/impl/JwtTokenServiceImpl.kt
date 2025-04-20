package com.theprasadtech.groundplay.services.impl

import com.theprasadtech.groundplay.services.JwtTokenService
import com.theprasadtech.groundplay.utils.logger
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

/**
 * Implementation of JwtTokenService for JWT token operations.
 * Uses the io.jsonwebtoken library for token generation, parsing, and validation.
 */
@Service
class JwtTokenServiceImpl : JwtTokenService {
    private val log = logger()

    // Constants
    companion object {
        private const val FIFTEEN_DAY_IN_MS: Long = 15 * 86400000
    }

    @Value("\${jwt.secret}")
    private lateinit var jwtSecret: String

    private val jwtExpiration: Long = FIFTEEN_DAY_IN_MS

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }

    override fun generateToken(
        playerId: Long,
        phoneNumber: String,
    ): String {
        log.debug("Generating JWT token for player ID: $playerId")

        val now = Date()
        val expiryDate = Date(now.time + jwtExpiration)

        return Jwts
            .builder()
            .setSubject(playerId.toString())
            .claim("phoneNumber", phoneNumber)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }

    override fun validateToken(token: String): Boolean {
        try {
            Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (ex: SignatureException) {
            log.error("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            log.error("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            log.error("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            log.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            log.error("JWT claims string is empty")
        } catch (ex: Exception) {
            log.error("Error validating JWT token", ex)
        }

        return false
    }

    override fun getPlayerIdFromToken(token: String): Long {
        try {
            val claims = getAllClaimsFromToken(token)
            return claims.subject.toLong()
        } catch (ex: Exception) {
            log.error("Error extracting player ID from token", ex)
            throw IllegalArgumentException("Could not extract player ID from token")
        }
    }

    override fun getPhoneNumberFromToken(token: String): String {
        try {
            val claims = getAllClaimsFromToken(token)
            return claims["phoneNumber"].toString()
        } catch (ex: Exception) {
            log.error("Error extracting phone number from token", ex)
            throw IllegalArgumentException("Could not extract phone number from token")
        }
    }

    override fun isTokenExpired(token: String): Boolean {
        try {
            val claims = getAllClaimsFromToken(token)
            val expiration = claims.expiration
            return expiration.before(Date())
        } catch (ex: ExpiredJwtException) {
            return true
        } catch (ex: Exception) {
            log.error("Error checking if token is expired", ex)
            throw IllegalArgumentException("Could not check token expiration")
        }
    }

    /**
     * Helper method to extract all claims from a JWT token.
     */
    private fun getAllClaimsFromToken(token: String): Claims =
        Jwts
            .parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
}
