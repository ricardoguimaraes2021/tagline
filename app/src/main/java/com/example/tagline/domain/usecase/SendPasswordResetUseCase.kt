package com.example.tagline.domain.usecase

import com.example.tagline.domain.repository.AuthRepository
import com.example.tagline.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

/**
 * Use case for sending password reset email.
 * Encapsulates the business logic for password recovery.
 */
class SendPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(email: String): Flow<Resource<Unit>> = flow {
        // Validation
        if (email.isBlank()) {
            emit(Resource.Error("Email é obrigatório"))
            return@flow
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emit(Resource.Error("Email inválido"))
            return@flow
        }
        
        emit(Resource.Loading())
        
        try {
            authRepository.sendPasswordResetEmail(email)
            emit(Resource.Success(Unit))
        } catch (e: IOException) {
            emit(Resource.Error("Erro de ligação. Verifique a internet"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erro ao enviar email de recuperação"))
        }
    }
}

