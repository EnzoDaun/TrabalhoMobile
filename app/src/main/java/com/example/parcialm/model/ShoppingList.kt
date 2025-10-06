// [RF003] Modelo de lista de compras vinculada ao usuário
package com.example.parcialm.model

data class ShoppingList(
    val id: String,
    val userId: String,
    val title: String,
    val imageUri: String? = null
)

