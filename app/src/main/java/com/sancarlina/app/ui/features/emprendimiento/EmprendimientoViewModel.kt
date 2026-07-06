package com.sancarlina.app.ui.features.emprendimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.SubmissionsRepository
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
        val sanitizedNombre = com.sancarlina.app.utils.InputValidator.sanitizeText(state.nombre, 100)
        val sanitizedCategoria = com.sancarlina.app.utils.InputValidator.sanitizeText(state.categoria, 50)
        val sanitizedTelefono = com.sancarlina.app.utils.InputValidator.sanitizeText(state.telefono, 25)
        val sanitizedUbicacion = com.sancarlina.app.utils.InputValidator.sanitizeText(state.ubicacion, 150)

        if (sanitizedNombre.isBlank() || sanitizedCategoria.isBlank() || sanitizedTelefono.isBlank() || sanitizedUbicacion.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa todos los campos obligatorios") }
            return
        }

        if (!com.sancarlina.app.utils.RateLimiter.isActionAllowed("submit_emprendimiento", 60000L)) {
            val remainingSecs = (com.sancarlina.app.utils.RateLimiter.getRemainingTime("submit_emprendimiento", 60000L) / 1000) + 1
            _uiState.update { it.copy(error = "Debes esperar $remainingSecs segundos antes de enviar otra solicitud.") }
            return
        }

        _uiState.update { 
            it.copy(
                isLoading = true, 
                error = null,
                nombre = sanitizedNombre,
                categoria = sanitizedCategoria,
                telefono = sanitizedTelefono,
                ubicacion = sanitizedUbicacion
            ) 
        }

        val requestData = mapOf(
            "userEmail" to (user.email ?: ""),
            "nombre" to sanitizedNombre,
            "categoria" to sanitizedCategoria,
            "telefono" to sanitizedTelefono,
            "ubicacion" to sanitizedUbicacion,
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
