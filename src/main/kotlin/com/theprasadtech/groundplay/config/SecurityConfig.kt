package com.theprasadtech.groundplay.config

import com.theprasadtech.groundplay.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Spring Security configuration for JWT-based stateless authentication.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {
    /**
     * Configures the security filter chain for the application.
     * - Disables CSRF protection (not needed for stateless JWT auth)
     * - Configures CORS with allowed origins, methods, headers
     * - Sets up public and protected endpoints
     * - Configures stateless session management
     * - Adds JWT authentication filter
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    // Public endpoints that don't require authentication
                    .requestMatchers("/auth/**")
                    .permitAll()
                    .requestMatchers("/v1/ping")
                    .permitAll()
                    .requestMatchers("/v1/games/nearby")
                    .permitAll()
                    .requestMatchers("/v1/games/*")
                    .permitAll()
                    .requestMatchers("/v1/gameDetails/*")
                    .permitAll()
                    .requestMatchers("/v1/players/*")
                    .permitAll()
                    // All other endpoints require authentication
                    .anyRequest()
                    .authenticated()
            }.sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings.
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration =
            CorsConfiguration().apply {
                allowedOrigins = listOf("*") // In production, specify actual origins
                allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                allowedHeaders = listOf("Authorization", "Content-Type", "X-Requested-With")
                exposedHeaders = listOf("Authorization")
                maxAge = 3600L
            }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}
