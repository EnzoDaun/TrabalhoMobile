// [RF003] [RF005] Implementação em memória do repositório de listas
package com.example.parcialm.repository

import com.example.parcialm.model.ShoppingList

class InMemoryListRepository : ListRepository {

    companion object {
        @Volatile
        private var instance: InMemoryListRepository? = null

        fun getInstance(): InMemoryListRepository {
            return instance ?: synchronized(this) {
                instance ?: InMemoryListRepository().also { instance = it }
            }
        }
    }

    private val lists = mutableMapOf<String, ShoppingList>()

    // [RF003] [RF005] Obter listas do usuário ordenadas A-Z
    override fun getListsByUserId(userId: String): List<ShoppingList> {
        return lists.values
            .filter { it.userId == userId }
            .sortedBy { it.title.lowercase() }
    }

    override fun getListById(listId: String): ShoppingList? {
        return lists[listId]
    }

    // [RF003] Criar nova lista
    override fun createList(list: ShoppingList): ShoppingList {
        lists[list.id] = list
        return list
    }

    // [RF003] Atualizar lista existente
    override fun updateList(list: ShoppingList): ShoppingList? {
        return if (lists.containsKey(list.id)) {
            lists[list.id] = list
            list
        } else null
    }

    // [RF003] Excluir lista (itens serão excluídos pelo ItemRepository)
    override fun deleteList(listId: String): Boolean {
        return lists.remove(listId) != null
    }
}

