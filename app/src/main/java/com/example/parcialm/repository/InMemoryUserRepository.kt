// [RF001] [RF002] Implementação em memória do repositório de usuários
package com.example.parcialm.repository

import com.example.parcialm.model.User
import java.util.UUID

class InMemoryUserRepository : UserRepository {

    companion object {
        @Volatile
        private var instance: InMemoryUserRepository? = null

        fun getInstance(): InMemoryUserRepository {
            return instance ?: synchronized(this) {
                instance ?: InMemoryUserRepository().also { instance = it }
            }
        }
    }

    private val users = mutableMapOf<String, User>()
    private var currentUser: User? = null

    // [RF001] Login com validação de credenciais
    override fun login(email: String, password: String): User? {
        val user = users[email]
        return if (user?.password == password) {
            currentUser = user
            user
        } else null
    }

    // [RF002] Registro de novo usuário
    override fun register(name: String, email: String, password: String): User? {
        if (users.containsKey(email)) return null

        val newUser = User(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email,
            password = password
        )
        users[email] = newUser
        currentUser = newUser
        return newUser
    }

    // [RF001] Obter usuário atual da sessão
    override fun getCurrentUser(): User? = currentUser

    // [RF001] Logout - limpar sessão
    override fun setCurrentUser(user: User?) {
        currentUser = user
    }

    override fun getUserByEmail(email: String): User? = users[email]
}

