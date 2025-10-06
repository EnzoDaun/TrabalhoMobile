// [RF004] Enumeração de categorias com ícones associados
package com.example.parcialm.model

import com.example.parcialm.R

enum class Category(val displayName: String, val iconRes: Int) {
    FRUTAS("Frutas", R.drawable.ic_category_fruits),
    VERDURAS("Verduras", R.drawable.ic_category_vegetables),
    CARNES("Carnes", R.drawable.ic_category_meat),
    LATICINIOS("Laticínios", R.drawable.ic_category_dairy),
    PADARIA("Padaria", R.drawable.ic_category_bakery),
    BEBIDAS("Bebidas", R.drawable.ic_category_drinks),
    LIMPEZA("Limpeza", R.drawable.ic_category_cleaning),
    HIGIENE("Higiene", R.drawable.ic_category_hygiene),
    OUTROS("Outros", R.drawable.ic_category_other);

    companion object {
        fun fromDisplayName(name: String): Category {
            return values().find { it.displayName == name } ?: OUTROS
        }
    }
}

