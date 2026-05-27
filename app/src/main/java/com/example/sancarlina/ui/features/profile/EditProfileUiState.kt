package com.example.sancarlina.ui.features.profile

data class EditProfileUiState(
    val fullName: String = "",
    val phone: String = "",
    val location: String = "La Consulta",
    val profileImageUrl: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

val locationsList = listOf("Eugenio Bustos", "La Consulta", "Villa de San Carlos", "Pareditas")
