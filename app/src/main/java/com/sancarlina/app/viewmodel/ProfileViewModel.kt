package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val user = auth.currentUser ?: return
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val doc = firestore.collection("userProfiles").document(user.uid).get().await()
                if (doc.exists()) {
                    _uiState.update { 
                        it.copy(
                            userName = doc.getString("user_name") ?: doc.getString("name") ?: "Usuario",
                            userEmail = doc.getString("email") ?: user.email ?: "",
                            pointsBalance = (doc.getLong("points_balance") ?: doc.getLong("points") ?: 0L).toInt(),
                            profileImageUrl = doc.getString("imageUrl") ?: doc.getString("photo_url"),
                            notificationCount = (doc.getLong("notification_count") ?: 0L).toInt(),
                            isLoading = false
                        )
                    }
                } else {
                    // Fallback to Auth data if profile doc doesn't exist yet
                    _uiState.update { 
                        it.copy(
                            userName = user.displayName ?: "Usuario",
                            userEmail = user.email ?: "",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Logger.e("Error loading profile", e)
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar el perfil") }
            }
        }
    }

    fun logout() {
        auth.signOut()
    }
}
