package com.sancarlina.app.ui.features.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.ui.features.notifications.SancarlinaNotification
import com.sancarlina.app.ui.theme.*

@Composable
fun NotificationItemCard(notification: SancarlinaNotification) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (notification.isNew) SancarlinaPrimary.copy(alpha = 0.04f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (notification.isNew) SancarlinaPrimary.copy(alpha = 0.12f) else SancarlinaSurfaceContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (notification.isNew) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = if (notification.isNew) SancarlinaPrimary else SancarlinaOutline
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (notification.isNew) FontWeight.SemiBold else FontWeight.Medium,
                    color = SancarlinaOnSurface
                )
                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SancarlinaOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = notification.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = SancarlinaOutline,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (notification.isNew) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(8.dp)
                        .background(SancarlinaSecondary, CircleShape)
                )
            }
        }
    }
    HorizontalDivider(
        color = SancarlinaOutlineVariant.copy(alpha = 0.3f),
        thickness = 0.5.dp
    )
}
