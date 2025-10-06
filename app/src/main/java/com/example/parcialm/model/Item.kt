// [RF004] Modelo de item com todos os campos necessários (maior peso)
package com.example.parcialm.model

data class Item(
    val id: String,
    val listId: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val category: String,
    val purchased: Boolean = false
)

