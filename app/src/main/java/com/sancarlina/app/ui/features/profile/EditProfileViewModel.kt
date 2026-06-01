package com.sancarlina.app.ui.features.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
        _uiState.update { it.copy(isLoading = true) }

        firestore.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    _uiState.update {
                        it.copy(
                            fullName = doc.getString("name") ?: "",
                            phone = doc.getString("phone") ?: "",
                            location = doc.getString("location") ?: "La Consulta",
                            profileImageUrl = doc.getString("imageUrl") ?: "",
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar datos") }
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
        _uiState.update { it.copy(isSaving = true) }

        val data = mapOf(
            "name" to _uiState.value.fullName,
            "phone" to _uiState.value.phone,
            "location" to _uiState.value.location
        )

        firestore.collection("users").document(user.uid).update(data)
            .addOnSuccessListener {
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(isSaving = false, error = "Error al guardar cambios") }
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
        _uiState.update { it.copy(isLoading = true, showDeleteDialog = false) }

        // 1. Delete from Firestore
        firestore.collection("users").document(user.uid).delete()
            .addOnSuccessListener {
                // 2. Delete from Auth
                user.delete()
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        _uiState.update { it.copy(isLoading = false, error = "Error al eliminar usuario. Re-inicie sesión e intente de nuevo.") }
                    }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(isLoading = false, error = "Error al eliminar datos") }
            }
    }
}
