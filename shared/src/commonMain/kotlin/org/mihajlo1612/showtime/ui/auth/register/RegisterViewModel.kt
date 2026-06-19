package org.mihajlo1612.showtime.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mihajlo1612.showtime.domain.repository.AuthRepository

data class RegisterUiState(
    val fullName: String = "",
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class RegisterUiEvent {
    data class FullNameChanged(val value: String) : RegisterUiEvent()
    data class UsernameChanged(val value: String) : RegisterUiEvent()
    data class PasswordChanged(val value: String) : RegisterUiEvent()
    data object RegisterClicked : RegisterUiEvent()
}

sealed class RegisterSideEffect {
    data object NavigateToHome : RegisterSideEffect()
}

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<RegisterSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onEvent(event: RegisterUiEvent) {
        when (event) {
            is RegisterUiEvent.FullNameChanged -> _uiState.update { it.copy(fullName = event.value) }
            is RegisterUiEvent.UsernameChanged -> _uiState.update { it.copy(username = event.value) }
            is RegisterUiEvent.PasswordChanged -> _uiState.update { it.copy(password = event.value) }
            is RegisterUiEvent.RegisterClicked -> register()
        }
    }

    private fun register() {
        val state = _uiState.value
        if (state.isLoading) return
        val validationError = when {
            state.fullName.isBlank() -> "Full name is required"
            state.username.length < 3 -> "Username must be at least 3 characters"
            !state.username.matches(Regex("^[a-zA-Z0-9_]+$")) ->
                "Username may contain only letters, digits and underscore"
            state.password.length < 8 -> "Password must be at least 8 characters"
            else -> null
        }
        if (validationError != null) {
            _uiState.update { it.copy(error = validationError) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                authRepository.register(state.fullName, state.username, state.password)
                _sideEffect.send(RegisterSideEffect.NavigateToHome)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Registration failed") }
            }
        }
    }
}