// [RF001] Activity de Login
package com.example.parcialm.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import com.example.parcialm.databinding.ActivityLoginBinding
import com.example.parcialm.repository.InMemoryItemRepository
import com.example.parcialm.repository.InMemoryListRepository
import com.example.parcialm.repository.InMemoryUserRepository
import com.example.parcialm.viewmodel.LoginViewModel
import com.example.parcialm.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory(
            InMemoryUserRepository.getInstance(),
            InMemoryListRepository.getInstance(),
            InMemoryItemRepository.getInstance(),
            SavedStateHandle()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [RNF] ViewBinding habilitado
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeLoginState()
    }

    private fun setupListeners() {
        // [RF001] Botão Entrar
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }

        // [RF001] Link para cadastro
        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is com.example.parcialm.viewmodel.LoginState.Success -> {
                        // [RF001] Login bem-sucedido - navegar para Suas Listas
                        val intent = Intent(this@LoginActivity, ShoppingListActivity::class.java)
                        intent.putExtra("USER_ID", state.user.id)
                        intent.putExtra("USER_NAME", state.user.name)
                        startActivity(intent)
                        finish()
                    }
                    is com.example.parcialm.viewmodel.LoginState.Error -> {
                        // [RF001] Exibir erro de validação
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
