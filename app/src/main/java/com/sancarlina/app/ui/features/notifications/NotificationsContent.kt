package com.sancarlina.app.ui.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.features.notifications.components.NotificationItemCard
import com.sancarlina.app.ui.features.notifications.components.NotificationsEmptyState
import com.sancarlina.app.ui.theme.SancarlinaBackground
import com.sancarlina.app.ui.theme.SancarlinaPrimary

@Composable
fun NotificationsContent(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    // Sin backend de notificaciones: lista vacía en runtime (no mocks en release).
    val notifications = remember { emptyList<SancarlinaNotification>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
    ) {
        SancarlinaTopBar(
            title = stringResource(R.string.notifications_title),
            onBack = onBack,
            actions = {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        Icons.Default.Settings,
                        stringResource(R.string.cd_settings),
                        tint = SancarlinaPrimary
                    )
                }
            }
        )

        if (notifications.isEmpty()) {
            NotificationsEmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->
                    NotificationItemCard(notification)
                }
            }
        }
    }
}

data class SancarlinaNotification(
    val id: Int,
    val title: String,
    val body: String,
    val time: String,
    val isNew: Boolean
)
