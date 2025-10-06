// [RF004] [RF005] Implementação em memória do repositório de itens
package com.example.parcialm.repository

import com.example.parcialm.model.Item

class InMemoryItemRepository : ItemRepository {

    companion object {
        @Volatile
        private var instance: InMemoryItemRepository? = null

        fun getInstance(): InMemoryItemRepository {
            return instance ?: synchronized(this) {
                instance ?: InMemoryItemRepository().also { instance = it }
            }
        }
    }

    private val items = mutableMapOf<String, Item>()

    // [RF004] [RF005] Obter itens da lista ordenados A-Z
    override fun getItemsByListId(listId: String): List<Item> {
        return items.values
            .filter { it.listId == listId }
            .sortedBy { it.name.lowercase() }
    }

    override fun getItemById(itemId: String): Item? {
        return items[itemId]
    }

    // [RF004] Criar novo item
    override fun createItem(item: Item): Item {
        items[item.id] = item
        return item
    }

    // [RF004] Atualizar item existente
    override fun updateItem(item: Item): Item? {
        return if (items.containsKey(item.id)) {
            items[item.id] = item
            item
        } else null
    }

    // [RF004] Excluir item
    override fun deleteItem(itemId: String): Boolean {
        return items.remove(itemId) != null
    }

    // [RF003] Excluir todos os itens de uma lista (quando lista é excluída)
    override fun deleteItemsByListId(listId: String): Int {
        val itemsToDelete = items.values.filter { it.listId == listId }
        itemsToDelete.forEach { items.remove(it.id) }
        return itemsToDelete.size
    }

    // [RF004] Marcar/desmarcar item como comprado
    override fun togglePurchased(itemId: String): Item? {
        val item = items[itemId] ?: return null
        val updated = item.copy(purchased = !item.purchased)
        items[itemId] = updated
        return updated
    }
}

