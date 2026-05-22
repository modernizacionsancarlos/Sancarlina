package com.example.sancarlina.ui.features.map

import com.google.android.gms.maps.model.LatLng

data class MapUiState(
    val markers: List<CommerceMarker> = emptyList(),
    val selectedMarker: CommerceMarker? = null,
    val isBottomSheetVisible: Boolean = false
)

data class CommerceMarker(
    val id: String,
    val name: String,
    val locationName: String, // E.g., San Carlos, Eugenio Bustos
    val position: LatLng,
    val category: String,
    val phone: String
)
