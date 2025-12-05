package com.example.tagline.data.repository

import com.example.tagline.util.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    val isLoggedIn: Boolean
        get() = currentUser != null

    fun login(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erro ao fazer login"))
        }
    }

    fun register(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erro ao criar conta"))
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erro ao enviar email de recuperação"))
        }
    }
}

