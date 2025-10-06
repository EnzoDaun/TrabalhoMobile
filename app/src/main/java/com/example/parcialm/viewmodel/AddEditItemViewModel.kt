// [RF004] ViewModel de Adicionar/Editar Item
package com.example.parcialm.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcialm.model.Item
import com.example.parcialm.repository.ItemRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddEditItemViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _saveState = MutableSharedFlow<ItemSaveState>(replay = 1)
    val saveState: SharedFlow<ItemSaveState> = _saveState

    private val _itemFlow = MutableSharedFlow<Item?>(replay = 1)
    val itemFlow: SharedFlow<Item?> = _itemFlow

    // [RF004] Carregar item existente para edição
    fun loadItem(itemId: String?) {
        viewModelScope.launch {
            val item = itemId?.let { itemRepository.getItemById(it) }
            _itemFlow.emit(item)
        }
    }

    // [RF004] Salvar item com validações
    fun saveItem(
        itemId: String?,
        listId: String,
        name: String,
        quantity: String,
        unit: String,
        category: String
    ) {
        viewModelScope.launch {
            // Validar campos obrigatórios
            if (name.isBlank()) {
                _saveState.emit(ItemSaveState.Error("Nome é obrigatório"))
                return@launch
            }

            if (quantity.isBlank()) {
                _saveState.emit(ItemSaveState.Error("Quantidade é obrigatória"))
                return@launch
            }

            val quantityValue = quantity.toDoubleOrNull()
            if (quantityValue == null || quantityValue <= 0) {
                _saveState.emit(ItemSaveState.Error("Quantidade deve ser um número positivo"))
                return@launch
            }

            if (unit.isBlank()) {
                _saveState.emit(ItemSaveState.Error("Unidade é obrigatória"))
                return@launch
            }

            if (category.isBlank()) {
                _saveState.emit(ItemSaveState.Error("Categoria é obrigatória"))
                return@launch
            }

            val item = if (itemId != null) {
                // Atualizar item existente
                val existing = itemRepository.getItemById(itemId)
                if (existing != null) {
                    val updated = existing.copy(
                        name = name,
                        quantity = quantityValue,
                        unit = unit,
                        category = category
                    )
                    itemRepository.updateItem(updated)
                    updated
                } else {
                    null
                }
            } else {
                // Criar novo item
                val newItem = Item(
                    id = UUID.randomUUID().toString(),
                    listId = listId,
                    name = name,
                    quantity = quantityValue,
                    unit = unit,
                    category = category,
                    purchased = false
                )
                itemRepository.createItem(newItem)
                newItem
            }

            if (item != null) {
                _saveState.emit(ItemSaveState.Success)
            } else {
                _saveState.emit(ItemSaveState.Error("Erro ao salvar item"))
            }
        }
    }
}

sealed class ItemSaveState {
    object Success : ItemSaveState()
    data class Error(val message: String) : ItemSaveState()
}

