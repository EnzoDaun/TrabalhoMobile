// [RF001] [RF002] Interface do repositório de usuários
package com.example.parcialm.repository

import com.example.parcialm.model.User

interface UserRepository {
    fun login(email: String, password: String): User?
    fun register(name: String, email: String, password: String): User?
    fun getCurrentUser(): User?
    fun setCurrentUser(user: User?)
    fun getUserByEmail(email: String): User?
}

