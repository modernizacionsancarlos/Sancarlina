package com.sancarlina.app.ui.features.profile

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

class EditProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val user = auth.currentUser ?: return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val doc = firestore.collection("userProfiles").document(user.uid).get().await()
                if (doc.exists()) {
                    _uiState.update {
                        it.copy(
                            fullName = doc.getString("user_name") ?: doc.getString("name") ?: "",
                            phone = doc.getString("phone") ?: "",
                            location = doc.getString("location") ?: "La Consulta",
                            profileImageUrl = doc.getString("imageUrl") ?: doc.getString("photo_url") ?: "",
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                Logger.e("Error loading user data", e)
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar datos") }
            }
        }
    }

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name) }
    }

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun onLocationChange(location: String) {
        _uiState.update { it.copy(location = location) }
    }

    fun saveProfile() {
        val user = auth.currentUser ?: return
        _uiState.update { it.copy(isSaving = true, error = null) }

        val data = mapOf(
            "user_name" to _uiState.value.fullName,
            "phone" to _uiState.value.phone,
            "location" to _uiState.value.location
        )

        viewModelScope.launch {
            try {
                firestore.collection("userProfiles").document(user.uid).update(data).await()
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                Logger.e("Error saving profile", e)
                _uiState.update { it.copy(isSaving = false, error = "Error al guardar cambios") }
            }
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun setShowDeleteDialog(show: Boolean) {
        _uiState.update { it.copy(showDeleteDialog = show) }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        val user = auth.currentUser ?: return
        _uiState.update { it.copy(isLoading = true, showDeleteDialog = false, error = null) }

        viewModelScope.launch {
            try {
                // 1. Delete from Firestore
                firestore.collection("userProfiles").document(user.uid).delete().await()
                
                // 2. Delete from Auth
                user.delete().await()
                
                onSuccess()
            } catch (e: Exception) {
                Logger.e("Error deleting account", e)
                _uiState.update { it.copy(isLoading = false, error = "Error al eliminar usuario. Re-inicie sesión e intente de nuevo.") }
            }
        }
    }
}
