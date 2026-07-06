package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Contrato mínimo para inyectar auth en LoginContent (prod + tests). */
interface LoginAuthViewModel {
    val uiState: StateFlow<AuthUiState>
    fun login(email: String, password: String, onSuccess: () -> Unit)
}

class AuthViewModel(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModel(), LoginAuthViewModel {

    private val _uiState = MutableStateFlow(AuthUiState())
    override val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    override fun login(email: String, password: String, onSuccess: () -> Unit) {
        val sanitizedEmail = com.sancarlina.app.utils.InputValidator.sanitizeText(email, 80)
        if (sanitizedEmail.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Completa todos los campos") }
            return
        }

        if (!com.sancarlina.app.utils.InputValidator.isValidEmail(sanitizedEmail)) {
            _uiState.update { it.copy(error = "Formato de correo electrónico inválido") }
            return
        }

        if (!com.sancarlina.app.utils.RateLimiter.isActionAllowed("login", 5000L)) {
            val remainingSecs = (com.sancarlina.app.utils.RateLimiter.getRemainingTime("login", 5000L) / 1000) + 1
            _uiState.update { it.copy(error = "Demasiados intentos de inicio de sesión. Reintenta en $remainingSecs segundos.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        auth.signInWithEmailAndPassword(sanitizedEmail, password)
            .addOnSuccessListener {
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
    }

    fun register(name: String, email: String, password: String, confirm: String, onSuccess: () -> Unit) {
        val sanitizedName = com.sancarlina.app.utils.InputValidator.sanitizeText(name, 50)
        val sanitizedEmail = com.sancarlina.app.utils.InputValidator.sanitizeText(email, 80)

        if (sanitizedName.isBlank() || sanitizedEmail.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Completa todos los campos") }
            return
        }

        if (!com.sancarlina.app.utils.InputValidator.isValidEmail(sanitizedEmail)) {
            _uiState.update { it.copy(error = "Formato de correo electrónico inválido") }
            return
        }

        if (password != confirm) {
            _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }

        if (password.length < 6) {
            _uiState.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        if (!com.sancarlina.app.utils.RateLimiter.isActionAllowed("register", 10000L)) {
            val remainingSecs = (com.sancarlina.app.utils.RateLimiter.getRemainingTime("register", 10000L) / 1000) + 1
            _uiState.update { it.copy(error = "Por favor espera $remainingSecs segundos antes de crear una cuenta.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        auth.createUserWithEmailAndPassword(sanitizedEmail, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                viewModelScope.launch {
                    try {
                        userRepository.syncUserProfile(uid, sanitizedEmail, sanitizedName)
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess()
                    } catch (e: Exception) {
                        _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                    }
                }
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
    }
}
