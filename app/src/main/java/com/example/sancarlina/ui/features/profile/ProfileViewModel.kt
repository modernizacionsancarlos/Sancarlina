package com.example.sancarlina.ui.features.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        _uiState.update { 
            it.copy(
                userName = "María Fernández",
                userEmail = "maria.fernandez@email.com",
                pointsBalance = 1250,
                profileImageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB5sExiGGPv7NLQVXTLBoriVswbU2UPNsyWy5QgimSOKrSDYnzvoMDyGVl1bWAcQrMbnBXcylJYRK36bltho-1rrBlAutupNAp5UVpH3T1jACCf6MDh9ejJ20YmXxttGF9qYVNIawUqVPUM_NkT5diNRVWfDGMfKuvU2QWMxI-UQSv19w4zz56LLnr5Xih6pwBsX3Nzmpzy2oL8Epwn_kySf4xftaR4prbfEwIjJbT1GogLEjHAvI8ts74ANkQzgM0_DdCw-8FQ3lg",
                notificationCount = 3
            )
        }
    }

    fun onLogoutClicked() {
        _uiState.update { it.copy(showLogoutConfirmation = true) }
    }

    fun confirmLogout() {
        // Logic to clear session
        _uiState.update { it.copy(showLogoutConfirmation = false) }
    }

    fun dismissLogout() {
        _uiState.update { it.copy(showLogoutConfirmation = false) }
    }
}
