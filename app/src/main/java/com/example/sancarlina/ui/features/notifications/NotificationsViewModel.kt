package com.example.sancarlina.ui.features.notifications

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        _uiState.update { 
            it.copy(
                newNotifications = listOf(
                    NotificationItem(
                        "1",
                        "¡Nueva oferta disponible!",
                        "15% off en regionales en Eugenio Bustos.",
                        "Hace 10 min",
                        false,
                        NotificationType.OFFER
                    )
                ),
                oldNotifications = listOf(
                    NotificationItem(
                        "2",
                        "",
                        "Se ha sumado un nuevo productor de miel artesanal cerca tuyo.",
                        "Ayer",
                        true,
                        NotificationType.MAP
                    ),
                    NotificationItem(
                        "3",
                        "",
                        "¡Tus puntos han sido acreditados con éxito!",
                        "Ayer",
                        true,
                        NotificationType.STAR
                    )
                )
            )
        }
    }

    fun markAsRead(id: String) {
        // Logic to mark as read in DB
    }
}
