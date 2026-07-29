package com.sancarlina.app.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.AdminBeneficiosRepository
import com.sancarlina.app.data.repository.Benefit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminBeneficiosUiState(
    val isLoading: Boolean = false,
    val benefits: List<Benefit> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class AdminBeneficiosViewModel(
    private val repository: AdminBeneficiosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminBeneficiosUiState())
    val uiState: StateFlow<AdminBeneficiosUiState> = _uiState.asStateFlow()

    init {
        loadBenefits()
    }

    fun loadBenefits() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getAllBenefits()
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        benefits = result.getOrDefault(emptyList())
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudieron cargar los beneficios."
                    )
                }
            }
        }
    }

    fun saveBenefit(benefit: Benefit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.saveBenefit(benefit)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, successMessage = "Beneficio guardado.") }
                loadBenefits()
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.localizedMessage ?: "Error al guardar beneficio."
                    )
                }
            }
        }
    }

    fun toggleActive(benefitId: String, currentActive: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.toggleActive(benefitId, !currentActive)
            if (result.isSuccess) {
                loadBenefits()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al actualizar estado del beneficio."
                    )
                }
            }
        }
    }

    fun deleteBenefit(benefitId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.deleteBenefit(benefitId)
            if (result.isSuccess) {
                loadBenefits()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al eliminar el beneficio."
                    )
                }
            }
        }
    }
}
