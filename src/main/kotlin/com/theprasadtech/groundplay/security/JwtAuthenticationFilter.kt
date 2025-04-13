package com.theprasadtech.groundplay.security

import com.theprasadtech.groundplay.services.JwtTokenService
import com.theprasadtech.groundplay.utils.logger
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenService: JwtTokenService,
) : OncePerRequestFilter() {
    private val log = logger()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            // Get authorization header
            val authHeader = request.getHeader("Authorization")

            // If no auth header or not a Bearer token, continue to next filter
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response)
                return
            }

            // Extract and validate the token
            val token = authHeader.substring(7) // Remove "Bearer " prefix

            if (jwtTokenService.validateToken(token)) {
                // If token is valid, extract player ID and set authentication
                val playerId = jwtTokenService.getPlayerIdFromToken(token)

                // Create authentication object with ROLE_USER authority
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                val authentication =
                    UsernamePasswordAuthenticationToken(
                        playerId,
                        null,
                        authorities,
                    )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                // Set the authentication in the Spring Security context
                SecurityContextHolder.getContext().authentication = authentication
                log.debug("Authentication set for player ID: $playerId")
            } else {
                log.debug("Invalid JWT token")
            }
        } catch (ex: Exception) {
            log.error("Error processing JWT authentication", ex)
        }

        filterChain.doFilter(request, response)
    }
}
