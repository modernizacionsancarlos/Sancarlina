package com.sancarlina.app.test

import androidx.lifecycle.ViewModel
import com.sancarlina.app.viewmodel.AuthUiState
import com.sancarlina.app.viewmodel.LoginAuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** ViewModel fake para smoke tests de login sin llamadas a Firebase. */
class FakeAuthViewModel : ViewModel(), LoginAuthViewModel {

    private val _uiState = MutableStateFlow(AuthUiState())
    override val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    override fun login(email: String, password: String, onSuccess: () -> Unit) {
        // No-op: el test no debe disparar autenticación real.
    }
}
