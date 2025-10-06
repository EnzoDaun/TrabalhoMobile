// [RF004] [RF005] ViewModel de Itens
package com.example.parcialm.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcialm.model.Item
import com.example.parcialm.model.ShoppingList
import com.example.parcialm.repository.ItemRepository
import com.example.parcialm.repository.ListRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ItemViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val itemRepository: ItemRepository,
    private val listRepository: ListRepository
) : ViewModel() {

    private val _itemsFlow = MutableSharedFlow<ItemsData>(replay = 1)
    val itemsFlow: SharedFlow<ItemsData> = _itemsFlow

    private val _eventFlow = MutableSharedFlow<ItemEvent>(replay = 1)
    val eventFlow: SharedFlow<ItemEvent> = _eventFlow

    private var currentListId: String = ""

    // [RF004] Carregar itens da lista ordenados A-Z e agrupados por categoria
    fun loadItems(listId: String) {
        currentListId = listId
        viewModelScope.launch {
            val list = listRepository.getListById(listId)
            val items = itemRepository.getItemsByListId(listId)

            // [RF004] Separar comprados e não comprados
            val notPurchased = items.filter { !it.purchased }
            val purchased = items.filter { it.purchased }

            _itemsFlow.emit(ItemsData(list, notPurchased, purchased))
        }
    }

    // [RF005] Buscar itens por nome (case-insensitive)
    fun searchItems(listId: String, query: String) {
        viewModelScope.launch {
            val list = listRepository.getListById(listId)
            val allItems = itemRepository.getItemsByListId(listId)

            val filtered = if (query.isBlank()) {
                allItems
            } else {
                allItems.filter { it.name.contains(query, ignoreCase = true) }
            }

            val notPurchased = filtered.filter { !it.purchased }
            val purchased = filtered.filter { it.purchased }

            _itemsFlow.emit(ItemsData(list, notPurchased, purchased))
        }
    }

    // [RF004] Marcar/desmarcar item como comprado
    fun togglePurchased(itemId: String) {
        viewModelScope.launch {
            itemRepository.togglePurchased(itemId)
            loadItems(currentListId)
        }
    }

    // [RF004] Excluir item
    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            itemRepository.deleteItem(itemId)
            loadItems(currentListId)
            _eventFlow.emit(ItemEvent.ItemDeleted)
        }
    }
}

data class ItemsData(
    val list: ShoppingList?,
    val notPurchasedItems: List<Item>,
    val purchasedItems: List<Item>
)

sealed class ItemEvent {
    object ItemDeleted : ItemEvent()
}

