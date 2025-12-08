package com.example.tagline.domain.model

/**
 * Domain model for authentication result.
 * Pure Kotlin - no framework dependencies.
 */
data class AuthResult(
    val userId: String,
    val email: String?
)

