package com.example.tagline.domain.repository

import com.example.tagline.domain.model.AuthResult

/**
 * Repository interface for authentication operations.
 * Defined in domain layer - implemented in data layer.
 */
interface AuthRepository {
    
    val currentUserId: String?
    
    val isLoggedIn: Boolean
    
    suspend fun login(email: String, password: String): AuthResult
    
    suspend fun register(email: String, password: String): AuthResult
    
    fun logout()
    
    suspend fun sendPasswordResetEmail(email: String)
}

