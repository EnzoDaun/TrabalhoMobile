// [RF002] Activity de Cadastro
package com.example.parcialm.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import com.example.parcialm.databinding.ActivityRegisterBinding
import com.example.parcialm.repository.InMemoryItemRepository
import com.example.parcialm.repository.InMemoryListRepository
import com.example.parcialm.repository.InMemoryUserRepository
import com.example.parcialm.viewmodel.RegisterViewModel
import com.example.parcialm.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory(
            InMemoryUserRepository.getInstance(),
            InMemoryListRepository.getInstance(),
            InMemoryItemRepository.getInstance(),
            SavedStateHandle()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeRegisterState()
    }

    private fun setupListeners() {
        // [RF002] Botão Cadastrar
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            viewModel.register(name, email, password, confirmPassword)
        }

        // Botão Cancelar
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun observeRegisterState() {
        lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when (state) {
                    is com.example.parcialm.viewmodel.RegisterState.Success -> {
                        // [RF002] Cadastro bem-sucedido - navegar para Suas Listas
                        val intent = Intent(this@RegisterActivity, ShoppingListActivity::class.java)
                        intent.putExtra("USER_ID", state.user.id)
                        intent.putExtra("USER_NAME", state.user.name)
                        startActivity(intent)
                        finish()
                    }
                    is com.example.parcialm.viewmodel.RegisterState.Error -> {
                        // [RF002] Exibir erro de validação
                        Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

