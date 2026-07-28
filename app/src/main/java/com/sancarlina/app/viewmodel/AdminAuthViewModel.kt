package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.repository.AdminRepository
import com.sancarlina.app.utils.AdminAuthGuard
import com.sancarlina.app.utils.InputValidator
import com.sancarlina.app.utils.RateLimiter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminAuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

class AdminAuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminAuthUiState())
    val uiState: StateFlow<AdminAuthUiState> = _uiState.asStateFlow()

    fun loginAdmin(email: String, password: String, onSuccess: () -> Unit) {
        val sanitizedEmail = InputValidator.sanitizeText(email, 80)
        val cleanPassword = password.trim()

        if (sanitizedEmail.isBlank() || cleanPassword.isBlank()) {
            _uiState.update { it.copy(error = "Por favor, ingresa tu correo y contraseña.") }
            return
        }

        if (!InputValidator.isValidEmail(sanitizedEmail)) {
            _uiState.update { it.copy(error = "Credenciales inválidas o sin permisos de administración.") }
            return
        }

        // Limitación de tasa estricta: 5 intentos por cada 60 segundos
        if (!RateLimiter.isWindowAllowed("admin_login", 5, 60000L)) {
            val remainingSecs = (RateLimiter.getRemainingWindowTime("admin_login", 60000L) / 1000) + 1
            _uiState.update {
                it.copy(error = "Demasiados intentos de acceso administrativo. Reintenta en $remainingSecs segundos.")
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        auth.signInWithEmailAndPassword(sanitizedEmail, cleanPassword)
            .addOnSuccessListener {
                viewModelScope.launch {
                    val guardResult = AdminAuthGuard.verifyAdminAccess(auth, firestore)
                    if (guardResult.isSuccess) {
                        _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                        adminRepository.logAdminAction("admin_login_success")
                        onSuccess()
                    } else {
                        // Si no pasa la verificación de SuperAdmin o claim, cerrar sesión inmediatamente
                        auth.signOut()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Credenciales inválidas o sin permisos de administración."
                            )
                        }
                        adminRepository.logAdminAction("admin_login_unauthorized", mapOf("attempted_email" to sanitizedEmail))
                    }
                }
            }
            .addOnFailureListener {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Credenciales inválidas o sin permisos de administración."
                    )
                }
            }
    }

    fun logoutAdmin(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            adminRepository.logAdminAction("admin_logout")
            auth.signOut()
            _uiState.update { AdminAuthUiState() }
            onLogoutSuccess()
        }
    }
}
