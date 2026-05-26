package com.example.sancarlina.ui.features.category

import com.example.sancarlina.ui.features.map.CommerceMarker

data class CategoryListUiState(
    val categoryName: String = "",
    val commerces: List<CommerceMarker> = emptyList(),
    val filteredCommerces: List<CommerceMarker> = emptyList(),
    val locations: List<String> = emptyList(),
    val selectedLocation: String = "Todas",
    val isLoading: Boolean = false
)
