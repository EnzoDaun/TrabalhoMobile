// [RF003] ViewModel de Adicionar/Editar Lista
package com.example.parcialm.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcialm.model.ShoppingList
import com.example.parcialm.repository.ListRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddEditListViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val listRepository: ListRepository
) : ViewModel() {

    // [RNF] Uso de SharedFlow com replay=1
    private val _saveState = MutableSharedFlow<SaveState>(replay = 1)
    val saveState: SharedFlow<SaveState> = _saveState

    private val _listFlow = MutableSharedFlow<ShoppingList?>(replay = 1)
    val listFlow: SharedFlow<ShoppingList?> = _listFlow

    // [RF003] Carregar lista existente para edição
    fun loadList(listId: String?) {
        viewModelScope.launch {
            val list = listId?.let { listRepository.getListById(it) }
            _listFlow.emit(list)
        }
    }

    // [RF003] Salvar lista (criar ou atualizar) com validação de título obrigatório
    fun saveList(listId: String?, userId: String, title: String, imageUri: String?) {
        viewModelScope.launch {
            // Validar título obrigatório
            if (title.isBlank()) {
                _saveState.emit(SaveState.Error("Título é obrigatório"))
                return@launch
            }

            val list = if (listId != null) {
                // Atualizar lista existente
                val existing = listRepository.getListById(listId)
                if (existing != null) {
                    val updated = existing.copy(title = title, imageUri = imageUri)
                    listRepository.updateList(updated)
                    updated
                } else {
                    null
                }
            } else {
                // Criar nova lista
                val newList = ShoppingList(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    title = title,
                    imageUri = imageUri
                )
                listRepository.createList(newList)
                newList
            }

            if (list != null) {
                _saveState.emit(SaveState.Success)
            } else {
                _saveState.emit(SaveState.Error("Erro ao salvar lista"))
            }
        }
    }
}

sealed class SaveState {
    object Success : SaveState()
    data class Error(val message: String) : SaveState()
}

