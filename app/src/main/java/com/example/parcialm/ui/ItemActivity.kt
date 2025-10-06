// [RF004] [RF005] Activity de Itens
package com.example.parcialm.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parcialm.R
import com.example.parcialm.databinding.ActivityItemBinding
import com.example.parcialm.repository.InMemoryItemRepository
import com.example.parcialm.repository.InMemoryListRepository
import com.example.parcialm.repository.InMemoryUserRepository
import com.example.parcialm.viewmodel.ItemViewModel
import com.example.parcialm.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class ItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemBinding
    private lateinit var adapter: ItemAdapter
    private var listId: String = ""
    private var userId: String = ""

    private val viewModel: ItemViewModel by viewModels {
        ViewModelFactory(
            InMemoryUserRepository.getInstance(),
            InMemoryListRepository.getInstance(),
            InMemoryItemRepository.getInstance(),
            SavedStateHandle()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listId = intent.getStringExtra("LIST_ID") ?: ""
        userId = intent.getStringExtra("USER_ID") ?: ""

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        // [RF004] Carregar itens da lista
        viewModel.loadItems(listId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.items_title)

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    // [RF004] RecyclerView com Adapter
    private fun setupRecyclerView() {
        adapter = ItemAdapter(
            onItemClick = { item ->
                // [RF004] Marcar/desmarcar comprado
                viewModel.togglePurchased(item.id)
            },
            onEditClick = { item ->
                // [RF004] Editar item
                val intent = Intent(this, AddEditItemActivity::class.java)
                intent.putExtra("ITEM_ID", item.id)
                intent.putExtra("LIST_ID", listId)
                startActivity(intent)
            },
            onDeleteClick = { item ->
                // [RF004] Confirmar exclusão
                showDeleteConfirmation(item.id)
            }
        )

        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.adapter = adapter
    }

    private fun setupListeners() {
        // [RF004] FAB para adicionar item
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddEditItemActivity::class.java)
            intent.putExtra("LIST_ID", listId)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.itemsFlow.collect { data ->
                // [RF004] Exibir imagem da lista quando houver
                data.list?.let { list ->
                    supportActionBar?.subtitle = list.title
                    if (list.imageUri != null) {
                        try {
                            binding.ivListImage.setImageURI(Uri.parse(list.imageUri))
                            binding.ivListImage.visibility = android.view.View.VISIBLE
                        } catch (e: Exception) {
                            binding.ivListImage.visibility = android.view.View.GONE
                        }
                    }
                }

                // [RF004] Agrupar itens por categoria e separar comprados
                val groupedItems = mutableListOf<ItemAdapter.ItemDisplay>()

                // Seção de Pendentes (não comprados)
                if (data.notPurchasedItems.isNotEmpty()) {
                    groupedItems.add(ItemAdapter.ItemDisplay.Header(getString(R.string.not_purchased_section)))

                    // Agrupar por categoria
                    val byCategory = data.notPurchasedItems.groupBy { it.category }
                    byCategory.keys.sorted().forEach { category ->
                        groupedItems.add(ItemAdapter.ItemDisplay.CategoryHeader(category))
                        byCategory[category]?.forEach { item ->
                            groupedItems.add(ItemAdapter.ItemDisplay.ItemData(item))
                        }
                    }
                }

                // Seção de Comprados
                if (data.purchasedItems.isNotEmpty()) {
                    groupedItems.add(ItemAdapter.ItemDisplay.Header(getString(R.string.purchased_section)))

                    // Agrupar por categoria
                    val byCategory = data.purchasedItems.groupBy { it.category }
                    byCategory.keys.sorted().forEach { category ->
                        groupedItems.add(ItemAdapter.ItemDisplay.CategoryHeader(category))
                        byCategory[category]?.forEach { item ->
                            groupedItems.add(ItemAdapter.ItemDisplay.ItemData(item))
                        }
                    }
                }

                adapter.submitList(groupedItems)

                binding.tvEmptyState.visibility = if (groupedItems.isEmpty()) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is com.example.parcialm.viewmodel.ItemEvent.ItemDeleted -> {
                        Toast.makeText(this@ItemActivity, R.string.item_deleted, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // [RNF] Recarregar itens ao voltar
        viewModel.loadItems(listId)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)

        // [RF005] Configurar busca
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_items_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // [RF005] Buscar itens por nome
                viewModel.searchItems(listId, newText ?: "")
                return true
            }
        })

        return true
    }

    // [RF004] Confirmar exclusão de item
    private fun showDeleteConfirmation(itemId: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_item)
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteItem(itemId)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
}
