package com.sancarlina.app.viewmodel

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUserLoggedIn: Boolean = false
)
