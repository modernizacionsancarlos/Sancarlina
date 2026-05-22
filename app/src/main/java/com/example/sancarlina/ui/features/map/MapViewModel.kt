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
        _uiState.update { 
            it.copy(
                markers = listOf(
                    CommerceMarker(
                        id = "1",
                        name = "Bodega La Celia",
                        locationName = "Eugenio Bustos",
                        position = LatLng(-33.7667, -69.1000),
                        category = "Bodegas",
                        phone = "5492622000001"
                    ),
                    CommerceMarker(
                        id = "2",
                        name = "Miel Sancarlina",
                        locationName = "San Carlos",
                        position = LatLng(-33.7483, -69.0436),
                        category = "Productores",
                        phone = "5492622000000"
                    ),
                    CommerceMarker(
                        id = "3",
                        name = "Finca El Retiro",
                        locationName = "La Consulta",
                        position = LatLng(-33.7333, -69.1167),
                        category = "Turismo",
                        phone = "5492622000002"
                    )
                )
            )
        }
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
