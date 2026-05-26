package com.example.sancarlina.ui.features.turismo

data class TurismoUiState(
    val experiences: List<ExperienceItem> = emptyList(),
    val isLoading: Boolean = false
)

data class ExperienceItem(
    val id: String,
    val title: String,
    val location: String,
    val imageUrl: String,
    val category: String,
    val description: String
)
