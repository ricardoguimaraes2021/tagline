package com.example.tagline.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagline.domain.repository.AuthRepository
import com.example.tagline.domain.usecase.LoginUseCase
import com.example.tagline.domain.usecase.RegisterUseCase
import com.example.tagline.domain.usecase.SendPasswordResetUseCase
import com.example.tagline.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isLoggedIn: Boolean
        get() = authRepository.isLoggedIn

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = null
        )
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            errorMessage = null
        )
    }

    fun login() {
        val state = _uiState.value

        loginUseCase(state.email, state.password)
            .onEach { result ->
                _uiState.value = when (result) {
                    is Resource.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is Resource.Success -> _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                    is Resource.Error -> _uiState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = result.message
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun register() {
        val state = _uiState.value

        registerUseCase(state.email, state.password, state.confirmPassword)
            .onEach { result ->
                _uiState.value = when (result) {
                    is Resource.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is Resource.Success -> _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                    is Resource.Error -> _uiState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = result.message
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun sendPasswordReset() {
        val state = _uiState.value

        sendPasswordResetUseCase(state.email)
            .onEach { result ->
                _uiState.value = when (result) {
                    is Resource.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is Resource.Success -> _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                    is Resource.Error -> _uiState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = result.message
                    )
                }
            }
            .launchIn(viewModelScope)
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
}
