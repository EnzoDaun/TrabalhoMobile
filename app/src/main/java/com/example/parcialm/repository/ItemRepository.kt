// [RF004] Interface do repositório de itens
package com.example.parcialm.repository

import com.example.parcialm.model.Item

interface ItemRepository {
    fun getItemsByListId(listId: String): List<Item>
    fun getItemById(itemId: String): Item?
    fun createItem(item: Item): Item
    fun updateItem(item: Item): Item?
    fun deleteItem(itemId: String): Boolean
    fun deleteItemsByListId(listId: String): Int
    fun togglePurchased(itemId: String): Item?
}

