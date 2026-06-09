package com.sancarlina.app.ui.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sancarlina.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsContent(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val notifications = remember {
        listOf(
            SancarlinaNotification(1, "¡Nuevos puntos!", "Has sumado 50 puntos en Bodega La Celia.", "Hace 2 horas", true),
            SancarlinaNotification(2, "Feria Regional", "Este fin de semana no te pierdas la feria de productores.", "Hace 5 horas", false),
            SancarlinaNotification(3, "Perfil verificado", "Tu emprendimiento ha sido verificado con éxito.", "Ayer", false)
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(SancarlinaSurface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SancarlinaSurfaceContainerLow
            ) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SancarlinaPrimary)
                    }
                    Text(
                        text = "Notificaciones",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, null, tint = SancarlinaPrimary)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(notification)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: SancarlinaNotification) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        color = if (notification.isNew) SancarlinaPrimary.copy(alpha = 0.03f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (notification.isNew) SancarlinaPrimary.copy(alpha = 0.1f) else SancarlinaSurfaceContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (notification.isNew) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                        null,
                        tint = if (notification.isNew) SancarlinaPrimary else SancarlinaOutline
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (notification.isNew) FontWeight.Bold else FontWeight.Medium,
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
    HorizontalDivider(color = SancarlinaOutlineVariant.copy(alpha = 0.3f), thickness = 0.5.dp)
}

data class SancarlinaNotification(
    val id: Int,
    val title: String,
    val body: String,
    val time: String,
    val isNew: Boolean
)
