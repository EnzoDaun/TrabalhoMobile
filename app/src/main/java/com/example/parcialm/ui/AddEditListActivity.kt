// [RF003] Activity de Adicionar/Editar Lista
package com.example.parcialm.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import com.example.parcialm.R
import com.example.parcialm.databinding.ActivityAddEditListBinding
import com.example.parcialm.repository.InMemoryItemRepository
import com.example.parcialm.repository.InMemoryListRepository
import com.example.parcialm.repository.InMemoryUserRepository
import com.example.parcialm.viewmodel.AddEditListViewModel
import com.example.parcialm.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.io.File

class AddEditListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditListBinding
    private var userId: String = ""
    private var listId: String? = null
    private var selectedImageUri: String? = null
    private var photoUri: Uri? = null

    private val viewModel: AddEditListViewModel by viewModels {
        ViewModelFactory(
            InMemoryUserRepository.getInstance(),
            InMemoryListRepository.getInstance(),
            InMemoryItemRepository.getInstance(),
            SavedStateHandle()
        )
    }

    // [RF003] SAF para seleção de imagem
    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // [RNF] Armazenar URI como string em memória
            selectedImageUri = it.toString()
            binding.ivPreview.setImageURI(it)
            binding.ivPreview.visibility = android.view.View.VISIBLE
        }
    }

    // Launcher para solicitar permissão de câmera
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, R.string.camera_permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para tirar foto
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && photoUri != null) {
            selectedImageUri = photoUri.toString()
            binding.ivPreview.setImageURI(photoUri)
            binding.ivPreview.visibility = android.view.View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID") ?: ""
        listId = intent.getStringExtra("LIST_ID")

        setupToolbar()
        setupListeners()
        observeViewModel()

        // [RF003] Carregar lista existente se for edição
        listId?.let { viewModel.loadList(it) }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (listId == null) {
            getString(R.string.add_list_title)
        } else {
            getString(R.string.edit_list_title)
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupListeners() {
        // Botão para tirar foto
        binding.btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndTakePhoto()
        }

        // [RF003] Botão para selecionar imagem
        binding.btnSelectImage.setOnClickListener {
            selectImageLauncher.launch(arrayOf("image/*"))
        }

        // [RF003] Botão Salvar
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            viewModel.saveList(listId, userId, title, selectedImageUri)
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun checkCameraPermissionAndTakePhoto() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        // Criar arquivo temporário para a foto
        val photoFile = File.createTempFile(
            "shopping_list_",
            ".jpg",
            cacheDir
        ).apply {
            deleteOnExit()
        }

        photoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )

        takePictureLauncher.launch(photoUri)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.listFlow.collect { list ->
                list?.let {
                    binding.etTitle.setText(it.title)
                    selectedImageUri = it.imageUri
                    it.imageUri?.let { uriString ->
                        try {
                            binding.ivPreview.setImageURI(Uri.parse(uriString))
                            binding.ivPreview.visibility = android.view.View.VISIBLE
                        } catch (e: Exception) {
                            binding.ivPreview.setImageResource(R.drawable.ic_placeholder)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.saveState.collect { state ->
                when (state) {
                    is com.example.parcialm.viewmodel.SaveState.Success -> {
                        finish()
                    }
                    is com.example.parcialm.viewmodel.SaveState.Error -> {
                        Toast.makeText(this@AddEditListActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
