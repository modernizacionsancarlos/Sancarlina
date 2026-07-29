package com.sancarlina.app.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.AdminZonasRepository
import com.sancarlina.app.data.repository.Area
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminZonasUiState(
    val isLoading: Boolean = false,
    val areas: List<Area> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class AdminZonasViewModel(
    private val repository: AdminZonasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminZonasUiState())
    val uiState: StateFlow<AdminZonasUiState> = _uiState.asStateFlow()

    init {
        loadAreas()
    }

    fun loadAreas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getAllAreas()
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        areas = result.getOrDefault(emptyList())
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudieron cargar las zonas turísticas."
                    )
                }
            }
        }
    }

    fun ensureSuggestedAreas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.ensureSuggestedAreas()
            if (result.isSuccess) {
                val added = result.getOrDefault(0)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = if (added > 0) "Se cargaron $added zonas sugeridas." else "Todas las zonas sugeridas ya están creadas."
                    )
                }
                loadAreas()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al sembrar zonas sugeridas."
                    )
                }
            }
        }
    }

    fun saveArea(area: Area, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.saveArea(area)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, successMessage = "Zona guardada.") }
                loadAreas()
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.localizedMessage ?: "Error al guardar zona."
                    )
                }
            }
        }
    }

    fun deleteArea(areaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.deleteArea(areaId)
            if (result.isSuccess) {
                loadAreas()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al eliminar la zona."
                    )
                }
            }
        }
    }
}
