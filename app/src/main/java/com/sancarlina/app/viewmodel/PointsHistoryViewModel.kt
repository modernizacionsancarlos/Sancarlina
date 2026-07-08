package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sancarlina.app.data.models.PointMovement
import com.sancarlina.app.data.repository.UserRepository
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PointsHistoryUiState(
    val movements: List<PointMovement> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class PointsHistoryViewModel(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PointsHistoryUiState())
    val uiState: StateFlow<PointsHistoryUiState> = _uiState.asStateFlow()

    init {
        loadMovements()
    }

    fun loadMovements() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _uiState.update { it.copy(error = "Iniciá sesión para ver tu historial", movements = emptyList(), isLoading = false) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val list = userRepository.getPointMovements(uid)
                _uiState.update { it.copy(movements = list, isLoading = false) }
            } catch (e: Exception) {
                Logger.e("Error fetching point movements history", e)
                _uiState.update { it.copy(error = "No se pudo cargar el historial", isLoading = false) }
            }
        }
    }
}
