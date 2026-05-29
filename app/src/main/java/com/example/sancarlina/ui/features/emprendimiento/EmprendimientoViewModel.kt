package com.example.sancarlina.ui.features.emprendimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sancarlina.data.repository.SubmissionsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmprendimientoViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val submissionsRepository = SubmissionsRepository(firestore, auth)

    private val _uiState = MutableStateFlow(EmprendimientoUiState())
    val uiState: StateFlow<EmprendimientoUiState> = _uiState.asStateFlow()

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value) }
    }

    fun onCategoriaChange(value: String) {
        _uiState.update { it.copy(categoria = value) }
    }

    fun onTelefonoChange(value: String) {
        _uiState.update { it.copy(telefono = value) }
    }

    fun onUbicacionChange(value: String) {
        _uiState.update { it.copy(ubicacion = value) }
    }

    fun onImageSelected(uri: String) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun submitRequest() {
        val user = auth.currentUser
        if (user == null) {
            _uiState.update { it.copy(error = "Debes iniciar sesión para enviar una solicitud") }
            return
        }

        val state = _uiState.value
        if (state.nombre.isBlank() || state.categoria.isBlank() || state.telefono.isBlank() || state.ubicacion.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa todos los campos obligatorios") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        val requestData = mapOf(
            "userEmail" to (user.email ?: ""),
            "nombre" to state.nombre,
            "categoria" to state.categoria,
            "telefono" to state.telefono,
            "ubicacion" to state.ubicacion,
            "status" to "pending"
        )

        viewModelScope.launch {
            val result = submissionsRepository.submitForm("emprendimiento_solicitud", requestData)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al enviar: ${result.exceptionOrNull()?.message}") }
            }
        }
    }

    fun resetState() {
        _uiState.value = EmprendimientoUiState()
    }
}
