package com.example.tagline.domain.usecase

import com.example.tagline.domain.model.AuthResult
import com.example.tagline.domain.repository.AuthRepository
import com.example.tagline.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

/**
 * Use case for user login.
 * Encapsulates the business logic for authentication.
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<Resource<AuthResult>> = flow {
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
        
        emit(Resource.Loading())
        
        try {
            val result = authRepository.login(email, password)
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
            message.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) -> "Email ou password incorretos"
            message.contains("USER_NOT_FOUND", ignoreCase = true) -> "Utilizador não encontrado"
            message.contains("WRONG_PASSWORD", ignoreCase = true) -> "Password incorreta"
            message.contains("TOO_MANY_REQUESTS", ignoreCase = true) -> "Demasiadas tentativas. Tente mais tarde"
            message.contains("NETWORK", ignoreCase = true) -> "Erro de ligação. Verifique a internet"
            else -> message
        }
    }
}

