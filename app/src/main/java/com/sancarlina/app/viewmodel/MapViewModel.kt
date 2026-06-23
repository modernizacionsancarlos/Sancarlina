package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.BuildConfig
import com.sancarlina.app.data.models.displayImageUrl
import com.sancarlina.app.data.repository.AreasRepository
import com.sancarlina.app.data.repository.TenantsRepository
import com.sancarlina.app.utils.Logger
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    private val tenantsRepository: TenantsRepository,
    private val areasRepository: AreasRepository
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadMarkers()
    }

    private fun loadMarkers() {
        viewModelScope.launch {
            try {
                val tenants = tenantsRepository.getActiveTenants()
                val areas = areasRepository.getAreas()
                
                val markers = tenants.mapNotNull { tenant ->
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
                        null // Skip invalid markers
                    }
                }

                if (markers.isNotEmpty()) {
                    val categories = listOf("Todos") + markers.map { it.category }.distinct()
                    val locations = listOf("Todas") + areas.map { it.name }.distinct()

                    _uiState.update { 
                        it.copy(
                            markers = markers,
                            filteredMarkers = markers,
                            categories = categories,
                            locations = locations
                        )
                    }
                } else {
                    clearMarkers()
                }
            } catch (e: Exception) {
                Logger.e("Error loading map markers", e)
                clearMarkers()
            }
        }
    }

    private fun clearMarkers() {
        _uiState.update {
            it.copy(
                markers = emptyList(),
                filteredMarkers = emptyList(),
                categories = listOf("Todos"),
                locations = listOf("Todas")
            )
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { state ->
            val updatedState = state.copy(selectedCategory = category)
            updatedState.copy(filteredMarkers = applyFilters(updatedState))
        }
    }

    fun onLocationSelected(location: String) {
        _uiState.update { state ->
            val updatedState = state.copy(selectedLocation = location)
            updatedState.copy(filteredMarkers = applyFilters(updatedState))
        }
    }

    fun onSelloToggled(enabled: Boolean) {
        _uiState.update { state ->
            val updatedState = state.copy(onlyWithSello = enabled)
            updatedState.copy(filteredMarkers = applyFilters(updatedState))
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val updatedState = state.copy(searchQuery = query)
            updatedState.copy(filteredMarkers = applyFilters(updatedState))
        }
    }

    private fun applyFilters(state: MapUiState): List<CommerceMarker> {
        return state.markers.filter { marker ->
            val categoryMatch = state.selectedCategory == "Todos" || marker.category.equals(state.selectedCategory, ignoreCase = true)
            val locationMatch = state.selectedLocation == "Todas" || marker.locationName.equals(state.selectedLocation, ignoreCase = true)
            val queryMatch = state.searchQuery.isBlank() || 
                marker.name.contains(state.searchQuery, ignoreCase = true) || 
                marker.category.contains(state.searchQuery, ignoreCase = true) ||
                marker.locationName.contains(state.searchQuery, ignoreCase = true)
            val selloMatch = !state.onlyWithSello
            categoryMatch && locationMatch && queryMatch && selloMatch
        }
    }

    fun toggleFilterPanel(visible: Boolean) {
        _uiState.update { it.copy(isFilterPanelVisible = visible) }
    }

    fun clearFilters() {
        _uiState.update { state ->
            val updatedState = state.copy(
                selectedCategory = "Todos",
                selectedLocation = "Todas",
                onlyWithSello = false,
                searchQuery = ""
            )
            updatedState.copy(filteredMarkers = updatedState.markers)
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(isLocationPermissionGranted = granted) }
    }

    fun onMarkerClick(marker: CommerceMarker) {
        _uiState.update { 
            it.copy(
                selectedMarker = marker,
                isBottomSheetVisible = true
            )
        }
    }

    fun onDismissBottomSheet() {
        _uiState.update { 
            it.copy(isBottomSheetVisible = false)
        }
    }
}
