package com.example.sancarlina.ui.features.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(isUserLoggedIn = auth.currentUser != null) }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa todos los campos") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isUserLoggedIn = true) }
                    onSuccess()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = task.exception?.localizedMessage ?: "Error al iniciar sesión") }
                }
            }
    }

    fun register(name: String, email: String, pass: String, confirmPass: String, onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa todos los campos") }
            return
        }

        if (pass != confirmPass) {
            _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Here we would also save the name to Firestore or update profile
                    _uiState.update { it.copy(isLoading = false, isUserLoggedIn = true) }
                    onSuccess()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = task.exception?.localizedMessage ?: "Error al registrarse") }
                }
            }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
