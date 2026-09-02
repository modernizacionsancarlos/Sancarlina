package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sancarlina.app.data.repository.NotificationsRepository
import com.sancarlina.app.ui.features.notifications.SancarlinaNotification
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val notifications: List<SancarlinaNotification> = emptyList(),
    val isLoading: Boolean = false
)

class NotificationsViewModel(
    auth: FirebaseAuth,
    notificationsRepository: NotificationsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUiState(isLoading = true))
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _uiState.value = NotificationsUiState(isLoading = false)
        } else {
            viewModelScope.launch {
                notificationsRepository.observeNotifications(uid).collect { remote ->
                    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val now = System.currentTimeMillis()
                    _uiState.value = NotificationsUiState(
                        notifications = remote.map { item ->
                            val timestamp = item.timestamp?.toDate()
                            SancarlinaNotification(
                                id = item.id,
                                title = item.title,
                                body = item.message,
                                time = timestamp?.let(formatter::format).orEmpty(),
                                isNew = timestamp != null && now - timestamp.time < 24 * 60 * 60 * 1000L
                            )
                        },
                        isLoading = false
                    )
                }
            }
        }
    }
}
