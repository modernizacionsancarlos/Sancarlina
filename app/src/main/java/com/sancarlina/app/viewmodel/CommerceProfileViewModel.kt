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
import com.sancarlina.app.data.repository.EngagementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
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
    private val formsRepository: FormsRepository,
    private val engagementRepository: EngagementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommerceProfileUiState())
    val uiState: StateFlow<CommerceProfileUiState> = _uiState.asStateFlow()
    private var syncJob: kotlinx.coroutines.Job? = null

    fun loadCommerce(commerceId: String) {
        if (commerceId.isBlank()) {
            _uiState.update { it.copy(error = "ID de comercio no válido") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            val favoriteIdsFlow = auth.currentUser?.uid
                ?.let(userRepository::observeFavoriteTenantIds)
                ?: flowOf(emptyList())
            combine(
                tenantsRepository.observeTenant(commerceId),
                formsRepository.observeFormsByTenant(commerceId),
                favoriteIdsFlow
            ) { tenant, allForms, favoriteTenantIds ->
                Triple(
                    tenant,
                    allForms.filter { form ->
                        form.isPublic && form.acceptsResponses && form.status != "archived"
                    },
                    favoriteTenantIds.contains(commerceId)
                )
            }.collect { (tenant, filteredForms, isFav) ->
                _uiState.update { 
                    it.copy(
                        tenant = tenant,
                        forms = filteredForms,
                        isFavorite = isFav,
                        isLoading = false
                    )
                }
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

    fun trackAction(action: String) {
        val tenant = _uiState.value.tenant ?: return
        viewModelScope.launch {
            runCatching {
                engagementRepository.trackTenantAction(tenant.id, tenant.name, action)
            }.onFailure { Logger.e("Error tracking tenant action", it) }
        }
    }
}
