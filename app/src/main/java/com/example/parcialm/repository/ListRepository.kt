// [RF003] Interface do repositório de listas
package com.example.parcialm.repository

import com.example.parcialm.model.ShoppingList

interface ListRepository {
    fun getListsByUserId(userId: String): List<ShoppingList>
    fun getListById(listId: String): ShoppingList?
    fun createList(list: ShoppingList): ShoppingList
    fun updateList(list: ShoppingList): ShoppingList?
    fun deleteList(listId: String): Boolean
}

