// [RNF] ViewModel Factory para injeção de dependências
package com.example.parcialm.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.parcialm.repository.ItemRepository
import com.example.parcialm.repository.ListRepository
import com.example.parcialm.repository.UserRepository

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val listRepository: ListRepository,
    private val itemRepository: ItemRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(savedStateHandle, userRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(savedStateHandle, userRepository) as T
            }
            modelClass.isAssignableFrom(ShoppingListViewModel::class.java) -> {
                ShoppingListViewModel(listRepository, itemRepository) as T
            }
            modelClass.isAssignableFrom(AddEditListViewModel::class.java) -> {
                AddEditListViewModel(savedStateHandle, listRepository) as T
            }
            modelClass.isAssignableFrom(ItemViewModel::class.java) -> {
                ItemViewModel(savedStateHandle, itemRepository, listRepository) as T
            }
            modelClass.isAssignableFrom(AddEditItemViewModel::class.java) -> {
                AddEditItemViewModel(savedStateHandle, itemRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

