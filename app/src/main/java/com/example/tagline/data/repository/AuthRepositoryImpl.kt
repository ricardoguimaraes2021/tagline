package com.example.tagline.data.repository

import com.example.tagline.domain.model.AuthResult
import com.example.tagline.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    
    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    override val isLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null

    override suspend fun login(email: String, password: String): AuthResult {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return AuthResult(
            userId = result.user?.uid ?: throw IllegalStateException("User ID is null"),
            email = result.user?.email
        )
    }

    override suspend fun register(email: String, password: String): AuthResult {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        return AuthResult(
            userId = result.user?.uid ?: throw IllegalStateException("User ID is null"),
            email = result.user?.email
        )
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }
}

