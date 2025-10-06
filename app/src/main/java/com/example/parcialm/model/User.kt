// [RF001] [RF002] Modelo de usuário para autenticação e cadastro
package com.example.parcialm.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String
)

