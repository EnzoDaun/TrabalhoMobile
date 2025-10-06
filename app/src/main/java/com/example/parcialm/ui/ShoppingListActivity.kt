// [RF003] [RF005] Activity de Listas
package com.example.parcialm.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parcialm.R
import com.example.parcialm.databinding.ActivityShoppingListBinding
import com.example.parcialm.repository.InMemoryItemRepository
import com.example.parcialm.repository.InMemoryListRepository
import com.example.parcialm.repository.InMemoryUserRepository
import com.example.parcialm.viewmodel.ShoppingListViewModel
import com.example.parcialm.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class ShoppingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingListBinding
    private lateinit var adapter: ShoppingListAdapter
    private var userId: String = ""
    private var userName: String = ""

    private val viewModel: ShoppingListViewModel by viewModels {
        ViewModelFactory(
            InMemoryUserRepository.getInstance(),
            InMemoryListRepository.getInstance(),
            InMemoryItemRepository.getInstance(),
            SavedStateHandle()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [RNF] Recuperar dados do usuário
        userId = intent.getStringExtra("USER_ID") ?: ""
        userName = intent.getStringExtra("USER_NAME") ?: ""

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        // [RF003] Carregar listas do usuário
        viewModel.loadLists(userId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.lists_title)
        supportActionBar?.subtitle = "Olá, $userName"
    }

    // [RF003] RecyclerView com Adapter
    private fun setupRecyclerView() {
        adapter = ShoppingListAdapter(
            onItemClick = { list ->
                // [RF003] Abrir tela de itens da lista
                val intent = Intent(this, ItemActivity::class.java)
                intent.putExtra("LIST_ID", list.id)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            },
            onEditClick = { list ->
                // [RF003] Editar lista
                val intent = Intent(this, AddEditListActivity::class.java)
                intent.putExtra("LIST_ID", list.id)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            },
            onDeleteClick = { list ->
                // [RF003] Confirmar exclusão
                showDeleteConfirmation(list.id)
            }
        )

        binding.rvLists.layoutManager = LinearLayoutManager(this)
        binding.rvLists.adapter = adapter
    }

    private fun setupListeners() {
        // [RF003] FAB para adicionar lista
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddEditListActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
    }
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.listsFlow.collect { lists ->
                adapter.submitList(lists)
                binding.tvEmptyState.visibility = if (lists.isEmpty()) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is com.example.parcialm.viewmodel.ListEvent.ListDeleted -> {
                        Toast.makeText(this@ShoppingListActivity, R.string.list_deleted, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // [RNF] Recarregar listas ao voltar (mantém estado após rotação)
        viewModel.loadLists(userId)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_shopping_list, menu)

        // [RF005] Configurar busca
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_lists_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // [RF005] Buscar listas por título
                viewModel.searchLists(userId, newText ?: "")
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // [RF001] Logout
                showLogoutConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // [RF003] Confirmar exclusão de lista
    private fun showDeleteConfirmation(listId: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_list)
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteList(listId)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    // [RF001] Confirmar logout
    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(R.string.action_logout)
            .setMessage(R.string.logout_confirmation)
            .setPositiveButton(R.string.yes) { _, _ ->
                // Limpar sessão e voltar ao login
                InMemoryUserRepository.getInstance().setCurrentUser(null)
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
}
