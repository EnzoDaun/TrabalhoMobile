// [RF001] ViewModel de Login
package com.example.parcialm.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcialm.model.User
import com.example.parcialm.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _loginState = MutableSharedFlow<LoginState>(replay = 1)
    val loginState: SharedFlow<LoginState> = _loginState

    // [RF001] Validação de e-mail (regex simples)
    private fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
    }

    // [RF001] Login com validações
    fun login(email: String, password: String) {
        viewModelScope.launch {
            // Validar campos não vazios
            if (email.isBlank() || password.isBlank()) {
                _loginState.emit(LoginState.Error("E-mail e senha são obrigatórios"))
                return@launch
            }

            // Validar formato do e-mail
            if (!isValidEmail(email)) {
                _loginState.emit(LoginState.Error("E-mail inválido"))
                return@launch
            }

            // Tentar autenticar
            val user = userRepository.login(email, password)
            if (user != null) {
                _loginState.emit(LoginState.Success(user))
            } else {
                _loginState.emit(LoginState.Error("E-mail ou senha incorretos"))
            }
        }
    }
}

sealed class LoginState {
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

