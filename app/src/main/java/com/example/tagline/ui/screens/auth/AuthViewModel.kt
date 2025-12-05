package com.example.tagline.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagline.data.repository.AuthRepository
import com.example.tagline.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isLoggedIn: Boolean
        get() = authRepository.isLoggedIn

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = null,
            errorMessage = null
        )
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null,
            errorMessage = null
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = null,
            errorMessage = null
        )
    }

    fun login() {
        val state = _uiState.value
        
        if (!validateEmail(state.email)) return
        if (!validatePassword(state.password)) return

        viewModelScope.launch {
            authRepository.login(state.email, state.password).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = mapFirebaseError(result.message)
                        )
                    }
                }
            }
        }
    }

    fun register() {
        val state = _uiState.value
        
        if (!validateEmail(state.email)) return
        if (!validatePassword(state.password)) return
        if (!validateConfirmPassword(state.password, state.confirmPassword)) return

        viewModelScope.launch {
            authRepository.register(state.email, state.password).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = mapFirebaseError(result.message)
                        )
                    }
                }
            }
        }
    }

    fun sendPasswordReset() {
        val state = _uiState.value
        
        if (!validateEmail(state.email)) return

        viewModelScope.launch {
            authRepository.sendPasswordResetEmail(state.email).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = mapFirebaseError(result.message)
                        )
                    }
                }
            }
        }
    }

    fun logout() {
        authRepository.logout()
        resetState()
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun validateEmail(email: String): Boolean {
        return if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(emailError = "Email é obrigatório")
            false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(emailError = "Email inválido")
            false
        } else {
            true
        }
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(passwordError = "Password é obrigatória")
            false
        } else if (password.length < 6) {
            _uiState.value = _uiState.value.copy(passwordError = "Password deve ter pelo menos 6 caracteres")
            false
        } else {
            true
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return if (confirmPassword != password) {
            _uiState.value = _uiState.value.copy(confirmPasswordError = "As passwords não coincidem")
            false
        } else {
            true
        }
    }

    private fun mapFirebaseError(message: String?): String {
        return when {
            message == null -> "Ocorreu um erro desconhecido"
            message.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) -> "Email ou password incorretos"
            message.contains("EMAIL_EXISTS", ignoreCase = true) -> "Este email já está registado"
            message.contains("WEAK_PASSWORD", ignoreCase = true) -> "Password demasiado fraca"
            message.contains("INVALID_EMAIL", ignoreCase = true) -> "Email inválido"
            message.contains("USER_NOT_FOUND", ignoreCase = true) -> "Utilizador não encontrado"
            message.contains("WRONG_PASSWORD", ignoreCase = true) -> "Password incorreta"
            message.contains("TOO_MANY_REQUESTS", ignoreCase = true) -> "Demasiadas tentativas. Tente mais tarde"
            message.contains("NETWORK", ignoreCase = true) -> "Erro de ligação. Verifique a internet"
            else -> message
        }
    }
}

