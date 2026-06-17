package com.sancarlina.app.ui.features.category

import com.sancarlina.app.viewmodel.CommerceMarker

data class CategoryListUiState(
    val categoryName: String = "",
    val commerces: List<CommerceMarker> = emptyList(),
    val filteredCommerces: List<CommerceMarker> = emptyList(),
    val locations: List<String> = emptyList(),
    val selectedLocation: String = "Todas",
    val isLoading: Boolean = false,
    val hasLoadError: Boolean = false
)
