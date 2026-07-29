package com.sancarlina.app.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.AdminUsuariosRepository
import com.sancarlina.app.data.repository.UserProfileAdmin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUsuariosUiState(
    val isLoading: Boolean = false,
    val users: List<UserProfileAdmin> = emptyList(),
    val filteredUsers: List<UserProfileAdmin> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null,
    val successMessage: String? = null
)

class AdminUsuariosViewModel(
    private val repository: AdminUsuariosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUsuariosUiState())
    val uiState: StateFlow<AdminUsuariosUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getAllUsers()
            if (result.isSuccess) {
                val list = result.getOrDefault(emptyList())
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        users = list,
                        filteredUsers = filterUsers(list, currentState.searchQuery)
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudieron cargar los perfiles de usuario."
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredUsers = filterUsers(currentState.users, query)
            )
        }
    }

    fun resetPassword(uid: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.resetPassword(uid, newPassword)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, successMessage = "Contraseña restablecida exitosamente.") }
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.localizedMessage ?: "Error al restablecer la contraseña."
                    )
                }
            }
        }
    }

    fun setSuperAdmin(uid: String, email: String, active: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.setSuperAdmin(uid, email, active)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, successMessage = "Rol de administrador actualizado.") }
                loadUsers()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.localizedMessage ?: "Error al modificar permisos de admin."
                    )
                }
            }
        }
    }

    private fun filterUsers(list: List<UserProfileAdmin>, query: String): List<UserProfileAdmin> {
        if (query.isBlank()) return list
        return list.filter { u ->
            u.user_name.contains(query, ignoreCase = true) ||
                    u.email.contains(query, ignoreCase = true) ||
                    u.phone.contains(query, ignoreCase = true) ||
                    u.uid.contains(query, ignoreCase = true)
        }
    }
}
