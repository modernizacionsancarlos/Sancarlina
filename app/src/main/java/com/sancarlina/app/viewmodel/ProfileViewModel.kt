package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.repository.UserRepository
import com.sancarlina.app.utils.AdminAuthGuard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var profileJob: Job? = null
    private var adminAccessJob: Job? = null
    private val authStateListener = FirebaseAuth.AuthStateListener {
        loadUserProfile()
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    private fun loadUserProfile() {
        profileJob?.cancel()
        adminAccessJob?.cancel()
        val user = auth.currentUser
        if (user == null) {
            _uiState.update {
                ProfileUiState(
                    userName = "Explorador de San Carlos",
                    isLoggedIn = false,
                    isLoading = false
                )
            }
            return
        }
        _uiState.update {
            it.copy(
                isLoggedIn = true,
                isLoading = true,
                isCheckingAdminAccess = true,
                hasAdminAccess = false,
                hasFieldRegistrationAccess = false
            )
        }

        adminAccessJob = viewModelScope.launch {
            val hasAdminAccess = AdminAuthGuard.verifyAdminAccess(auth, firestore).isSuccess
            val claimRole = runCatching {
                user.getIdToken(false).await().claims["role"]?.toString().orEmpty()
            }.getOrDefault("")
            _uiState.update {
                it.copy(
                    hasAdminAccess = hasAdminAccess,
                    hasFieldRegistrationAccess = hasAdminAccess || claimRole.lowercase() in FIELD_STAFF_ROLES,
                    isCheckingAdminAccess = false
                )
            }
        }

        profileJob = viewModelScope.launch {
            userRepository.observeUserProfile(user.uid).collect { profile ->
                if (profile != null) {
                    _uiState.update { 
                        it.copy(
                            userName = profile.userName,
                            userEmail = profile.email.ifBlank { user.email.orEmpty() },
                            pointsBalance = profile.pointsBalance,
                            profileImageUrl = profile.profileImageUrl,
                            notificationCount = profile.notificationCount,
                            isLoggedIn = true,
                            hasFieldRegistrationAccess =
                                it.hasFieldRegistrationAccess || profile.role.lowercase() in FIELD_STAFF_ROLES,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            userName = user.displayName ?: "Usuario",
                            userEmail = user.email ?: "",
                            isLoggedIn = true,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun logout() {
        auth.signOut()
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authStateListener)
        super.onCleared()
    }

    companion object {
        private val FIELD_STAFF_ROLES =
            setOf("admin", "registrar", "registrador", "field_registrar", "staff")
    }
}
