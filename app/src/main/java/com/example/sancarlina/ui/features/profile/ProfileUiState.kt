package com.example.sancarlina.ui.features.profile

data class ProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val pointsBalance: Int = 0,
    val profileImageUrl: String = "",
    val notificationCount: Int = 0,
    val showLogoutConfirmation: Boolean = false
)
