package com.sancarlina.app.ui.features.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.model.FormSchema
import com.sancarlina.app.data.model.Tenant
import com.sancarlina.app.data.repository.FormsRepository
import com.sancarlina.app.data.repository.TenantsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CommerceProfileUiState(
    val tenant: Tenant? = null,
    val forms: List<FormSchema> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CommerceProfileViewModel(
    private val tenantsRepository: TenantsRepository,
    private val formsRepository: FormsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommerceProfileUiState())
    val uiState: StateFlow<CommerceProfileUiState> = _uiState.asStateFlow()

    fun loadCommerce(commerceId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                // 1. Fetch Tenant details (from cache if possible)
                val tenants = tenantsRepository.getActiveTenants()
                val tenant = tenants.find { it.id == commerceId }
                
                // 2. Fetch associated forms
                val forms = formsRepository.getFormsByTenant(commerceId)
                
                _uiState.update { 
                    it.copy(
                        tenant = tenant,
                        forms = forms,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
