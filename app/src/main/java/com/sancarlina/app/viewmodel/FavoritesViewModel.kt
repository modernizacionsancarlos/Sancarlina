package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.sancarlina.app.data.models.displayImageUrl
import com.sancarlina.app.data.repository.UserRepository
import com.sancarlina.app.data.repository.TenantsRepository
import com.sancarlina.app.data.repository.AreasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
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
    private var syncJob: kotlinx.coroutines.Job? = null

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

        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            combine(
                userRepository.observeFavoriteTenantIds(uid),
                tenantsRepository.observeActiveTenants(),
                areasRepository.getAreasFlow()
            ) { favIds, activeTenants, areas ->
                activeTenants.filter { it.id in favIds }.map { tenant ->
                    val lat = tenant.latitude ?: 0.0
                    val lng = tenant.longitude ?: 0.0
                        val areaName = areas.find { it.id == tenant.areaId }?.name ?: "General"
                        
                        CommerceMarker(
                            id = tenant.id,
                            name = tenant.name,
                            locationName = areaName,
                            position = LatLng(lat, lng),
                            category = tenant.industry,
                            phone = tenant.contactPhone,
                            imageUrl = tenant.displayImageUrl(),
                            rating = tenant.rating.toFloat(),
                            distance = "GondolApp"
                        )
                }
            }.collect { favMarkers ->
                _uiState.update { it.copy(favorites = favMarkers, isLoading = false) }
            }
        }
    }
}
