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
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Completa todos los campos") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
    }

    fun register(name: String, email: String, password: String, confirm: String, onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Completa todos los campos") }
            return
        }

        if (password != confirm) {
            _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                viewModelScope.launch {
                    try {
                        userRepository.syncUserProfile(uid, email, name)
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
