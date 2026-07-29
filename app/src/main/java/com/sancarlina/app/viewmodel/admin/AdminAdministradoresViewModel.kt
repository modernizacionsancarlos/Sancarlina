package com.sancarlina.app.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.models.SuperAdmin
import com.sancarlina.app.data.repository.AdminAdministradoresRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminAdministradoresUiState(
    val isLoading: Boolean = false,
    val superAdmins: List<SuperAdmin> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class AdminAdministradoresViewModel(
    private val repository: AdminAdministradoresRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminAdministradoresUiState())
    val uiState: StateFlow<AdminAdministradoresUiState> = _uiState.asStateFlow()

    init {
        loadSuperAdmins()
    }

    fun loadSuperAdmins() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val res = repository.getAllSuperAdmins()
            if (res.isSuccess) {
                _uiState.update { it.copy(isLoading = false, superAdmins = res.getOrDefault(emptyList())) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar super administradores.") }
            }
        }
    }

    fun createSuperAdminUser(email: String, password: String, userName: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank() || userName.isBlank()) {
            _uiState.update { it.copy(error = "Completa todos los campos requeridos.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val res = repository.createSuperAdminUser(email, password, userName)
            if (res.isSuccess) {
                _uiState.update { it.copy(isLoading = false, successMessage = "SuperAdmin creado exitosamente.") }
                loadSuperAdmins()
                onSuccess()
            } else {
                _uiState.update { it.copy(isLoading = false, error = res.exceptionOrNull()?.localizedMessage ?: "Error al crear SuperAdmin.") }
            }
        }
    }

    fun toggleSuperAdminActive(uid: String, email: String, currentActive: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val res = repository.toggleSuperAdminActive(uid, email, !currentActive)
            if (res.isSuccess) {
                loadSuperAdmins()
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al cambiar estado del SuperAdmin.") }
            }
        }
    }
}
