package com.example.sancarlina.ui.features.notifications

data class NotificationsUiState(
    val newNotifications: List<NotificationItem> = emptyList(),
    val oldNotifications: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = false
)

data class NotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val isRead: Boolean,
    val type: NotificationType
)

enum class NotificationType {
    OFFER, MAP, STAR, INFO
}
