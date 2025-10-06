// [RF004] Activity de Adicionar/Editar Item
package com.example.parcialm.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import com.example.parcialm.R
import com.example.parcialm.databinding.ActivityAddEditItemBinding
import com.example.parcialm.model.Category
import com.example.parcialm.repository.InMemoryItemRepository
import com.example.parcialm.repository.InMemoryListRepository
import com.example.parcialm.repository.InMemoryUserRepository
import com.example.parcialm.viewmodel.AddEditItemViewModel
import com.example.parcialm.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class AddEditItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditItemBinding
    private var listId: String = ""
    private var itemId: String? = null

    private val viewModel: AddEditItemViewModel by viewModels {
        ViewModelFactory(
            InMemoryUserRepository.getInstance(),
            InMemoryListRepository.getInstance(),
            InMemoryItemRepository.getInstance(),
            SavedStateHandle()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listId = intent.getStringExtra("LIST_ID") ?: ""
        itemId = intent.getStringExtra("ITEM_ID")

        setupToolbar()
        setupCategorySpinner()
        setupListeners()
        observeViewModel()

        // [RF004] Carregar item existente se for edição
        itemId?.let { viewModel.loadItem(it) }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (itemId == null) {
            getString(R.string.add_item_title)
        } else {
            getString(R.string.edit_item_title)
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    // [RF004] Configurar Spinner de categorias
    private fun setupCategorySpinner() {
        val adapter = CategorySpinnerAdapter(this, Category.values())
        binding.spinnerCategory.adapter = adapter
    }

    private fun setupListeners() {
        // [RF004] Botão Salvar
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val quantity = binding.etQuantity.text.toString()
            val unit = binding.etUnit.text.toString()
            val selectedCategory = binding.spinnerCategory.selectedItem as Category
            val category = selectedCategory.displayName

            viewModel.saveItem(itemId, listId, name, quantity, unit, category)
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.itemFlow.collect { item ->
                item?.let {
                    binding.etName.setText(it.name)
                    binding.etQuantity.setText(it.quantity.toString())
                    binding.etUnit.setText(it.unit)

                    // Selecionar categoria no spinner
                    val categoryIndex = Category.values()
                        .indexOfFirst { cat -> cat.displayName == it.category }
                    if (categoryIndex >= 0) {
                        binding.spinnerCategory.setSelection(categoryIndex)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.saveState.collect { state ->
                when (state) {
                    is com.example.parcialm.viewmodel.ItemSaveState.Success -> {
                        finish()
                    }
                    is com.example.parcialm.viewmodel.ItemSaveState.Error -> {
                        Toast.makeText(this@AddEditItemActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
