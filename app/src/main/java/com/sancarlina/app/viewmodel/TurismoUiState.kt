package com.sancarlina.app.viewmodel

data class TurismoUiState(
    val banners: List<BannerItem> = emptyList(),
    val points: List<TurismoPoint> = emptyList(),
    val categories: List<String> = listOf("Todos", "Naturaleza", "Cultura", "Aventura"),
    val selectedCategory: String = "Todos",
    val isLoading: Boolean = false
)

data class TurismoPoint(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val location: String = "San Carlos",
    val rating: Double = 0.0
)
