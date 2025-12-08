package com.example.tagline.domain.usecase

import com.example.tagline.domain.model.AuthResult
import com.example.tagline.domain.repository.AuthRepository
import com.example.tagline.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

/**
 * Use case for user registration.
 * Encapsulates the business logic for creating new accounts.
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): Flow<Resource<AuthResult>> = flow {
        // Validation
        if (email.isBlank()) {
            emit(Resource.Error("Email é obrigatório"))
            return@flow
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emit(Resource.Error("Email inválido"))
            return@flow
        }
        
        if (password.isBlank()) {
            emit(Resource.Error("Password é obrigatória"))
            return@flow
        }
        
        if (password.length < 6) {
            emit(Resource.Error("Password deve ter pelo menos 6 caracteres"))
            return@flow
        }
        
        if (password != confirmPassword) {
            emit(Resource.Error("As passwords não coincidem"))
            return@flow
        }
        
        emit(Resource.Loading())
        
        try {
            val result = authRepository.register(email, password)
            emit(Resource.Success(result))
        } catch (e: IOException) {
            emit(Resource.Error("Erro de ligação. Verifique a internet"))
        } catch (e: Exception) {
            emit(Resource.Error(mapFirebaseError(e.message)))
        }
    }
    
    private fun mapFirebaseError(message: String?): String {
        return when {
            message == null -> "Ocorreu um erro desconhecido"
            message.contains("EMAIL_EXISTS", ignoreCase = true) -> "Este email já está registado"
            message.contains("WEAK_PASSWORD", ignoreCase = true) -> "Password demasiado fraca"
            message.contains("INVALID_EMAIL", ignoreCase = true) -> "Email inválido"
            message.contains("NETWORK", ignoreCase = true) -> "Erro de ligação. Verifique a internet"
            else -> message
        }
    }
}

