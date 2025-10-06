// [RF002] ViewModel de Cadastro
package com.example.parcialm.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcialm.model.User
import com.example.parcialm.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _registerState = MutableSharedFlow<RegisterState>(replay = 1)
    val registerState: SharedFlow<RegisterState> = _registerState

    // [RF002] Validação de e-mail
    private fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
    }

    // [RF002] Cadastro com todas as validações
    fun register(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            // Validar campos não vazios
            if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                _registerState.emit(RegisterState.Error("Todos os campos são obrigatórios"))
                return@launch
            }

            // Validar formato do e-mail
            if (!isValidEmail(email)) {
                _registerState.emit(RegisterState.Error("E-mail inválido"))
                return@launch
            }

            // Validar senha = confirmar senha
            if (password != confirmPassword) {
                _registerState.emit(RegisterState.Error("As senhas não coincidem"))
                return@launch
            }

            // Validar tamanho mínimo de senha
            if (password.length < 6) {
                _registerState.emit(RegisterState.Error("Senha deve ter pelo menos 6 caracteres"))
                return@launch
            }

            // Tentar registrar
            val user = userRepository.register(name, email, password)
            if (user != null) {
                _registerState.emit(RegisterState.Success(user))
            } else {
                _registerState.emit(RegisterState.Error("E-mail já cadastrado"))
            }
        }
    }
}

sealed class RegisterState {
    data class Success(val user: User) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

