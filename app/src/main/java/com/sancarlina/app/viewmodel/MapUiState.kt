package com.sancarlina.app.viewmodel

import com.google.android.gms.maps.model.LatLng

data class MapUiState(
    val markers: List<CommerceMarker> = emptyList(),
    val filteredMarkers: List<CommerceMarker> = emptyList(),
    val categories: List<String> = emptyList(),
    val locations: List<String> = emptyList(),
    val selectedCategory: String = "Todos",
    val selectedLocation: String = "Todas",
    val onlyWithSello: Boolean = false,
    val selectedMarker: CommerceMarker? = null,
    val isBottomSheetVisible: Boolean = false,
    val isFilterPanelVisible: Boolean = false,
    val isLocationPermissionGranted: Boolean = false
)

data class CommerceMarker(
    val id: String,
    val name: String,
    val locationName: String,
    val position: LatLng,
    val category: String,
    val phone: String,
    val imageUrl: String = "",
    val rating: Float = 5f,
    val distance: String = "A 1.5 km de vos"
)
