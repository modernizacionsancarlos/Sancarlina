package com.sancarlina.app.ui.features.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.AreasRepository
import com.sancarlina.app.data.repository.TenantsRepository
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
            val tenants = tenantsRepository.getActiveTenants()
            val areas = areasRepository.getAreas()
            
            val markers = tenants.map { tenant ->
                val coords = tenant.geoCoordinates.split(",")
                val lat = coords.getOrNull(0)?.toDoubleOrNull() ?: 0.0
                val lng = coords.getOrNull(1)?.toDoubleOrNull() ?: 0.0
                
                val areaName = areas.find { it.id == tenant.areaId }?.name ?: "General"
                
                CommerceMarker(
                    id = tenant.id,
                    name = tenant.name,
                    locationName = areaName,
                    position = LatLng(lat, lng),
                    category = tenant.industry,
                    phone = tenant.contactEmail,
                    imageUrl = tenant.imageUrl.ifEmpty { tenant.coverUrl },
                    rating = tenant.rating.toFloat(),
                    distance = "Góndola Sancarlina"
                )
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
                loadMockMarkers()
            }
        }
    }

    private fun loadMockMarkers() {
        val markers = listOf(
            CommerceMarker(
                id = "1",
                name = "Bodega La Celia (Demo)",
                locationName = "Eugenio Bustos",
                position = LatLng(-33.7667, -69.1000),
                category = "Bodegas",
                phone = "5492622000001",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCLaIUm18ds1FBjC3LXxf2Zhn785L8x5ez3nxTETmXfmRZ5WBKziUoENgh2Tl7yz_o9NTyjz5JeEAeUwI5W4tAKnsPLjNaisLRlVmUQF4KI1kOKYsz_PuQXE4VHMnUvml64Yy1d8j2XDEQyMgXAM5pN6vnphSQH-Si5Orcx_258sThO3ImKxrGtP5lI6UEsCvRXbszPC2Ubdxyv1lesB9hu0bxezrD3XX8c2XnMA6GjH2wxjfdmsSes64CUjhzL8W9cmUp95DcI310",
                rating = 4.8f,
                distance = "A 2.1 km de vos"
            ),
            CommerceMarker(
                id = "2",
                name = "Miel Sancarlina (Demo)",
                locationName = "San Carlos",
                position = LatLng(-33.7483, -69.0436),
                category = "Productores",
                phone = "5492622000000",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCnRCqegozoJa59a1jH6WxldD6GuLGJoHEXDiukl1ZfA9XQf53RyNzXCcQfHi01RTKmvQp17R1VKsyJKgPyLaDxyo6OvGxkAkVHNSxVynC8PjwKR4qU2NMNKkJY6FuNLJ0HSyKk3vghFKFG1m8ygnWOaG5D-WiyLDH_HcHsV3jQah9G34mumg-2f9K-twlGyY53M_5SexZT5Li5kJSmczTlY690bL61FWFREj6vYpMgd6L0x2i3zycYwzZj-vV1b4DyFyEmF8BDXz8",
                rating = 5.0f,
                distance = "A 0.5 km de vos"
            )
        )
        val categories = listOf("Todos", "Bodegas", "Productores", "Turismo")
        val locations = listOf("Todas", "La Consulta", "Eugenio Bustos", "Villa de San Carlos")

        _uiState.update { 
            it.copy(
                markers = markers,
                filteredMarkers = markers,
                categories = categories,
                locations = locations
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

    private fun applyFilters(state: MapUiState): List<CommerceMarker> {
        return state.markers.filter { marker ->
            val categoryMatch = state.selectedCategory == "Todos" || marker.category == state.selectedCategory
            val locationMatch = state.selectedLocation == "Todas" || marker.locationName == state.selectedLocation
            val selloMatch = !state.onlyWithSello
            categoryMatch && locationMatch && selloMatch
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
                onlyWithSello = false
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
