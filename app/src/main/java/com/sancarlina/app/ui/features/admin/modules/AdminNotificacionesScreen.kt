package com.sancarlina.app.ui.features.admin.modules

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.data.repository.NotificationAdmin
import com.sancarlina.app.ui.components.SancarlinaElevatedCard
import com.sancarlina.app.ui.components.SancarlinaTextField
import com.sancarlina.app.ui.features.admin.components.AdminAddFab
import com.sancarlina.app.ui.features.admin.components.AdminMetricCard
import com.sancarlina.app.ui.features.admin.components.AdminScreenTopBar
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.admin.AdminNotificacionesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificacionesScreen(
    viewModel: AdminNotificacionesViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AdminScreenTopBar(title = "Administrar Notificaciones", onBack = onBack)
        },
        floatingActionButton = {
            AdminAddFab(label = "Nueva notificación", onClick = { showDialog = true })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            AdminMetricCard(
                label = "Notificaciones enviadas",
                value = uiState.notifications.size.toString(),
                modifier = Modifier.fillMaxWidth(),
                emphasized = true
            )
            Text(
                text = "Historial",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron notificaciones emitidas.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.notifications, key = { it.id }) { notif ->
                        NotificationAdminItem(
                            notification = notif,
                            onDelete = { viewModel.deleteNotification(notif.id) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        NewNotificationDialog(
            onDismiss = { showDialog = false },
            onSend = { title, message, target ->
                viewModel.sendNotification(title, message, target) {
                    showDialog = false
                }
            }
        )
    }
}

@Composable
private fun NotificationAdminItem(
    notification: NotificationAdmin,
    onDelete: () -> Unit
) {
    Card(
        shape = SancarlinaCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = notification.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = notification.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Destinatario: ${notification.target}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun NewNotificationDialog(
    onDismiss: () -> Unit,
    onSend: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("all") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Notificación") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SancarlinaTextField(value = title, onValueChange = { title = it }, label = "Título de la Alerta *")
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Mensaje o Contenido *") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        onSend(title, message, target)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Enviar Alerta") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
