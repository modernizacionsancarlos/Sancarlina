package com.sancarlina.app.viewmodel

data class ProfileUiState(
    val userName: String = "Cargando...",
    val userEmail: String = "",
    val pointsBalance: Int = 0,
    val profileImageUrl: String? = null,
    val notificationCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
