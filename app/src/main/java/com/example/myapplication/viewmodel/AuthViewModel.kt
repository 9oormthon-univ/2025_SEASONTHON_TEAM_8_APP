package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.AuthResponse
import com.example.myapplication.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun authenticateWithGoogle(code: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            authRepository
                    .authenticateWithGoogle(code)
                    .onSuccess { authResponse ->
                        _authState.value = AuthState.Success(authResponse)
                    }
                    .onFailure { exception ->
                        _authState.value =
                                AuthState.Error(exception.message ?: "알 수 없는 오류가 발생했습니다.")
                    }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val authResponse: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}
