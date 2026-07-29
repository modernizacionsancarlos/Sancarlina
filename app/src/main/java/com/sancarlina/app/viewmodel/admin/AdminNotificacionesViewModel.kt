package com.sancarlina.app.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.AdminNotificacionesRepository
import com.sancarlina.app.data.repository.NotificationAdmin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminNotificacionesUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationAdmin> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class AdminNotificacionesViewModel(
    private val repository: AdminNotificacionesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminNotificacionesUiState())
    val uiState: StateFlow<AdminNotificacionesUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val res = repository.getAllNotifications()
            if (res.isSuccess) {
                _uiState.update { it.copy(isLoading = false, notifications = res.getOrDefault(emptyList())) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar notificaciones.") }
            }
        }
    }

    fun sendNotification(title: String, message: String, target: String, onSuccess: () -> Unit) {
        if (title.isBlank() || message.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa título y mensaje.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val notif = NotificationAdmin(title = title, message = message, target = target)
            val res = repository.sendNotification(notif)
            if (res.isSuccess) {
                _uiState.update { it.copy(isLoading = false, successMessage = "Notificación enviada exitosamente.") }
                loadNotifications()
                onSuccess()
            } else {
                _uiState.update { it.copy(isLoading = false, error = res.exceptionOrNull()?.localizedMessage ?: "Error al enviar notificación.") }
            }
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            repository.deleteNotification(id)
            loadNotifications()
        }
    }
}
