package com.sancarlina.app.ui.features.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUserLoggedIn: Boolean = false
)
