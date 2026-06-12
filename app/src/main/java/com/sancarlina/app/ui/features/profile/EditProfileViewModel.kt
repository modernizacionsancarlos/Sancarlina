package com.sancarlina.app.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
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

    /** Paso 1: cierra advertencia y abre diálogo de contraseña. */
    fun proceedToDeletePassword() {
        _uiState.update {
            it.copy(showDeleteDialog = false, showDeletePasswordDialog = true, deletePassword = "", error = null)
        }
    }

    fun setShowDeletePasswordDialog(show: Boolean) {
        _uiState.update {
            it.copy(
                showDeletePasswordDialog = show,
                deletePassword = if (!show) "" else it.deletePassword
            )
        }
    }

    fun onDeletePasswordChange(password: String) {
        _uiState.update { it.copy(deletePassword = password) }
    }

    /**
     * Elimina cuenta tras re-autenticación (solo email/password).
     * Si en el futuro se agrega Google Sign-In, requiere flujo con GoogleAuthProvider.
     */
    fun deleteAccount(onSuccess: () -> Unit) {
        val user = auth.currentUser ?: return
        val email = user.email
        val password = _uiState.value.deletePassword

        if (email.isNullOrBlank()) {
            _uiState.update { it.copy(error = "No se encontró email asociado a la cuenta.") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(error = "Ingresá tu contraseña actual.") }
            return
        }

        _uiState.update {
            it.copy(isDeletingAccount = true, showDeletePasswordDialog = false, error = null)
        }

        viewModelScope.launch {
            try {
                val credential = EmailAuthProvider.getCredential(email, password)
                user.reauthenticate(credential).await()

                firestore.collection("userProfiles").document(user.uid).delete().await()
                user.delete().await()

                _uiState.update { it.copy(isDeletingAccount = false, deletePassword = "") }
                onSuccess()
            } catch (e: FirebaseAuthRecentLoginRequiredException) {
                Logger.e("Recent login required for delete", e)
                _uiState.update {
                    it.copy(
                        isDeletingAccount = false,
                        error = "Por seguridad, cerrá sesión, volvé a iniciar sesión e intentá de nuevo."
                    )
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Logger.e("Invalid credentials for delete", e)
                _uiState.update {
                    it.copy(
                        isDeletingAccount = false,
                        showDeletePasswordDialog = true,
                        error = "Contraseña incorrecta."
                    )
                }
            } catch (e: Exception) {
                Logger.e("Error deleting account", e)
                _uiState.update {
                    it.copy(
                        isDeletingAccount = false,
                        error = "Error al eliminar usuario. Re-inicie sesión e intente de nuevo."
                    )
                }
            }
        }
    }
}
