package com.sancarlina.app.ui.features.turismo

data class TurismoUiState(
    val featuredExperience: ExperienceItem? = null,
    val experiences: List<ExperienceItem> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "Todos",
    val isLoading: Boolean = false
)

data class ExperienceItem(
    val id: String,
    val title: String,
    val location: String,
    val imageUrl: String,
    val category: String,
    val rating: Float = 5.0f,
    val description: String
)
