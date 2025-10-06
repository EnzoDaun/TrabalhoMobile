// [RF003] [RF005] ViewModel de Listas
package com.example.parcialm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcialm.model.ShoppingList
import com.example.parcialm.repository.ItemRepository
import com.example.parcialm.repository.ListRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ShoppingListViewModel(
    private val listRepository: ListRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _listsFlow = MutableSharedFlow<List<ShoppingList>>(replay = 1)
    val listsFlow: SharedFlow<List<ShoppingList>> = _listsFlow

    private val _eventFlow = MutableSharedFlow<ListEvent>(replay = 1)
    val eventFlow: SharedFlow<ListEvent> = _eventFlow

    private var currentUserId: String = ""

    // [RF003] Carregar listas do usuário ordenadas A-Z
    fun loadLists(userId: String) {
        currentUserId = userId
        viewModelScope.launch {
            val lists = listRepository.getListsByUserId(userId)
            _listsFlow.emit(lists)
        }
    }

    // [RF005] Buscar listas por título (case-insensitive)
    fun searchLists(userId: String, query: String) {
        viewModelScope.launch {
            val allLists = listRepository.getListsByUserId(userId)
            val filtered = if (query.isBlank()) {
                allLists
            } else {
                allLists.filter { it.title.contains(query, ignoreCase = true) }
            }
            _listsFlow.emit(filtered)
        }
    }

    // [RF003] Excluir lista e seus itens
    fun deleteList(listId: String) {
        viewModelScope.launch {
            // Excluir itens da lista primeiro
            itemRepository.deleteItemsByListId(listId)
            // Excluir lista
            listRepository.deleteList(listId)
            // Recarregar listas
            loadLists(currentUserId)
            _eventFlow.emit(ListEvent.ListDeleted)
        }
    }
}

sealed class ListEvent {
    object ListDeleted : ListEvent()
}

