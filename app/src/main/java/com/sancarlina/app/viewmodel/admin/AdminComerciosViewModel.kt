package com.sancarlina.app.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.data.repository.AdminComerciosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminComerciosUiState(
    val isLoading: Boolean = false,
    val tenants: List<Tenant> = emptyList(),
    val filteredTenants: List<Tenant> = emptyList(),
    val searchQuery: String = "",
    val selectedStatusFilter: String = "all",
    val error: String? = null,
    val successMessage: String? = null
)

class AdminComerciosViewModel(
    private val repository: AdminComerciosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminComerciosUiState())
    val uiState: StateFlow<AdminComerciosUiState> = _uiState.asStateFlow()

    init {
        loadTenants()
    }

    fun loadTenants() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getAllTenants()
            if (result.isSuccess) {
                val list = result.getOrDefault(emptyList())
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        tenants = list,
                        filteredTenants = filterList(list, currentState.searchQuery, currentState.selectedStatusFilter)
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudieron cargar los comercios."
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredTenants = filterList(currentState.tenants, query, currentState.selectedStatusFilter)
            )
        }
    }

    fun onStatusFilterChanged(status: String) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedStatusFilter = status,
                filteredTenants = filterList(currentState.tenants, currentState.searchQuery, status)
            )
        }
    }

    fun saveTenant(tenant: Tenant, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.saveTenant(tenant)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, successMessage = "Comercio guardado exitosamente.") }
                loadTenants()
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.localizedMessage ?: "Error al guardar comercio."
                    )
                }
            }
        }
    }

    fun toggleTenantStatus(tenantId: String, currentStatus: String) {
        val newStatus = if (currentStatus == "active") "inactive" else "active"
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.updateTenantStatus(tenantId, newStatus)
            if (result.isSuccess) {
                loadTenants()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al actualizar estado del comercio."
                    )
                }
            }
        }
    }

    fun deleteTenant(tenantId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.deleteTenant(tenantId)
            if (result.isSuccess) {
                loadTenants()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al eliminar comercio."
                    )
                }
            }
        }
    }

    private fun filterList(list: List<Tenant>, query: String, statusFilter: String): List<Tenant> {
        return list.filter { tenant ->
            val matchesQuery = query.isBlank() ||
                    tenant.name.contains(query, ignoreCase = true) ||
                    tenant.industry.contains(query, ignoreCase = true) ||
                    tenant.address.contains(query, ignoreCase = true)

            val matchesStatus = when (statusFilter) {
                "active" -> tenant.status == "active"
                "inactive" -> tenant.status == "inactive"
                else -> true
            }

            matchesQuery && matchesStatus
        }
    }
}
