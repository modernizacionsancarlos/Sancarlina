package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.data.repository.FormsRepository
import com.sancarlina.app.data.repository.TenantsRepository
import com.sancarlina.app.utils.Logger
import com.google.firebase.auth.FirebaseAuth
import com.sancarlina.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CommerceProfileUiState(
    val tenant: Tenant? = null,
    val forms: List<FormSchema> = emptyList(),
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class CommerceProfileViewModel(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val tenantsRepository: TenantsRepository,
    private val formsRepository: FormsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommerceProfileUiState())
    val uiState: StateFlow<CommerceProfileUiState> = _uiState.asStateFlow()

    fun loadCommerce(commerceId: String) {
        if (commerceId.isBlank()) {
            _uiState.update { it.copy(error = "ID de comercio no válido") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                // 1. Fetch Tenant details (from cache if possible)
                val tenants = tenantsRepository.getActiveTenants()
                val tenant = tenants.find { it.id == commerceId }
                
                // 2. Fetch and filter associated forms
                val allForms = formsRepository.getFormsByTenant(commerceId)
                val filteredForms = allForms.filter { form ->
                    form.isPublic && form.acceptsResponses && form.status != "archived"
                }

                // 3. Fetch favorite status
                val uid = auth.currentUser?.uid
                val favoriteTenantIds = uid?.let { userRepository.getFavoriteTenantIds(it) } ?: emptyList()
                val isFav = favoriteTenantIds.contains(commerceId)
                
                _uiState.update { 
                    it.copy(
                        tenant = tenant,
                        forms = filteredForms,
                        isFavorite = isFav,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Logger.e("Error loading commerce profile", e)
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar la información") }
            }
        }
    }

    fun toggleFavorite() {
        val uid = auth.currentUser?.uid ?: return
        val tenant = _uiState.value.tenant ?: return
        
        viewModelScope.launch {
            try {
                val isNowFav = userRepository.toggleFavoriteTenant(uid, tenant.id)
                _uiState.update { it.copy(isFavorite = isNowFav) }
            } catch (e: Exception) {
                Logger.e("Error toggling favorite", e)
            }
        }
    }
}
