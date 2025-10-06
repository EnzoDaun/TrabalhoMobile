// [RF004] Enumeração de categorias com ícones associados
package com.example.parcialm.model

enum class Category(val displayName: String, val emoji: String) {
    FRUTAS("Frutas", "🍎"),
    VERDURAS("Verduras", "🥬"),
    CARNES("Carnes", "🍖"),
    LATICINIOS("Laticínios", "🧀"),
    PADARIA("Padaria", "🥖"),
    BEBIDAS("Bebidas", "🍹"),
    LIMPEZA("Limpeza", "🧽"),
    HIGIENE("Higiene", "🪥"),
    OUTROS("Outros", "❓");

    companion object {
        fun fromDisplayName(name: String): Category {
            return values().find { it.displayName == name } ?: OUTROS
        }
    }
}
