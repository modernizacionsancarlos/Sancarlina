package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.sancarlina.app.data.models.displayImageUrl
import com.sancarlina.app.data.repository.UserRepository
import com.sancarlina.app.data.repository.TenantsRepository
import com.sancarlina.app.data.repository.AreasRepository
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favorites: List<CommerceMarker> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class FavoritesViewModel(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val tenantsRepository: TenantsRepository,
    private val areasRepository: AreasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _uiState.update { it.copy(error = "Iniciá sesión para ver tus favoritos", favorites = emptyList(), isLoading = false) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // 1. Fetch user's favorite tenant IDs
                val favIds = userRepository.getFavoriteTenantIds(uid)
                
                if (favIds.isEmpty()) {
                    _uiState.update { it.copy(favorites = emptyList(), isLoading = false) }
                    return@launch
                }

                // 2. Fetch active tenants & areas
                val activeTenants = tenantsRepository.getActiveTenants()
                val areas = areasRepository.getAreas()

                // 3. Map matching tenants to CommerceMarker
                val favMarkers = activeTenants.filter { it.id in favIds }.mapNotNull { tenant ->
                    try {
                        val coords = tenant.geoCoordinates.split(",")
                        val lat = coords.getOrNull(0)?.toDoubleOrNull() ?: return@mapNotNull null
                        val lng = coords.getOrNull(1)?.toDoubleOrNull() ?: return@mapNotNull null
                        val areaName = areas.find { it.id == tenant.areaId }?.name ?: "General"
                        
                        CommerceMarker(
                            id = tenant.id,
                            name = tenant.name,
                            locationName = areaName,
                            position = LatLng(lat, lng),
                            category = tenant.industry,
                            phone = tenant.contactEmail,
                            imageUrl = tenant.displayImageUrl(),
                            rating = tenant.rating.toFloat(),
                            distance = "GondolApp"
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                _uiState.update { it.copy(favorites = favMarkers, isLoading = false) }
            } catch (e: Exception) {
                Logger.e("Error loading favorites", e)
                _uiState.update { it.copy(error = "No se pudieron cargar tus favoritos", isLoading = false) }
            }
        }
    }
}
