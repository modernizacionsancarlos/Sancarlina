package com.example.sancarlina.ui.features.emprendimiento

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EmprendimientoViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
            "userId" to user.uid,
            "userEmail" to user.email,
            "nombre" to state.nombre,
            "categoria" to state.categoria,
            "telefono" to state.telefono,
            "ubicacion" to state.ubicacion,
            "timestamp" to com.google.firebase.Timestamp.now(),
            "status" to "pending"
        )

        firestore.collection("emprendimiento_solicitudes").add(requestData)
            .addOnSuccessListener {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(isLoading = false, error = "Error al enviar: ${e.message}") }
            }
    }

    fun resetState() {
        _uiState.value = EmprendimientoUiState()
    }
}
