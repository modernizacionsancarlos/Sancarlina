package com.example.sancarlina.ui.features.map

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadMarkers()
    }

    private fun loadMarkers() {
        val markers = listOf(
            CommerceMarker(
                id = "1",
                name = "Bodega La Celia",
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
                name = "Miel Sancarlina",
                locationName = "San Carlos",
                position = LatLng(-33.7483, -69.0436),
                category = "Productores",
                phone = "5492622000000",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCnRCqegozoJa59a1jH6WxldD6GuLGJoHEXDiukl1ZfA9XQf53RyNzXCcQfHi01RTKmvQp17R1VKsyJKgPyLaDxyo6OvGxkAkVHNSxVynC8PjwKR4qU2NMNKkJY6FuNLJ0HSyKk3vghFKFG1m8ygnWOaG5D-WiyLDH_HcHsV3jQah9G34mumg-2f9K-twlGyY53M_5SexZT5Li5kJSmczTlY690bL61FWFREj6vYpMgd6L0x2i3zycYwzZj-vV1b4DyFyEmF8BDXz8",
                rating = 5.0f,
                distance = "A 0.5 km de vos"
            ),
            CommerceMarker(
                id = "3",
                name = "Finca El Retiro",
                locationName = "La Consulta",
                position = LatLng(-33.7333, -69.1167),
                category = "Turismo",
                phone = "5492622000002",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCLaIUm18ds1FBjC3LXxf2Zhn785L8x5ez3nxTETmXfmRZ5WBKziUoENgh2Tl7yz_o9NTyjz5JeEAeUwI5W4tAKnsPLjNaisLRlVmUQF4KI1kOKYsz_PuQXE4VHMnUvml64Yy1d8j2XDEQyMgXAM5pN6vnphSQH-Si5Orcx_258sThO3ImKxrGtP5lI6UEsCvRXbszPC2Ubdxyv1lesB9hu0bxezrD3XX8c2XnMA6GjH2wxjfdmsSes64CUjhzL8W9cmUp95DcI310",
                rating = 4.5f,
                distance = "A 3.2 km de vos"
            )
        )

        val categories = listOf("Todos") + markers.map { it.category }.distinct()
        val locations = listOf("Todas") + markers.map { it.locationName }.distinct()

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
            // Sello logic: for now markers don't have it, let's assume all have it or add it to CommerceMarker
            val selloMatch = !state.onlyWithSello // Simplified for now
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
