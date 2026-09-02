package com.sancarlina.app.ui.features.forms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sancarlina.app.data.models.OfflineSubmission
import com.sancarlina.app.data.models.SubmissionSyncStatus
import com.sancarlina.app.ui.components.SancarlinaTopBar

@Composable
fun FieldRegistrationContent(
    viewModel: FieldRegistrationViewModel,
    onBack: () -> Unit,
    onStartForm: (String) -> Unit,
    onEditSubmission: (String, String) -> Unit,
    onViewAllSubmissions: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val pendingCount = uiState.submissions.count { it.status != SubmissionSyncStatus.SENT }

    Scaffold(
        topBar = { SancarlinaTopBar(title = "Registro en calle", onBack = onBack) },
        bottomBar = {
            if (!uiState.accessDenied && uiState.forms.isNotEmpty()) {
                Surface(shadowElevation = 10.dp, color = MaterialTheme.colorScheme.surfaceContainerLowest) {
                    Button(
                        onClick = { uiState.selectedFormId?.let(onStartForm) },
                        enabled = uiState.selectedFormId != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .heightIn(min = 54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Iniciar formulario", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text("OPERATIVO MUNICIPAL", color = Color.White.copy(alpha = 0.78f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text("Relevá varios locales sin salir de esta sección", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 5.dp))
                        Text("Las respuestas quedan guardadas aunque pierdas conexión.", color = Color.White.copy(alpha = 0.86f), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 5.dp))
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    StatusSummaryCard("Habilitados", uiState.forms.size.toString(), Icons.Default.Description, Modifier.weight(1f))
                    StatusSummaryCard("Por enviar", pendingCount.toString(), Icons.Default.Schedule, Modifier.weight(1f), pendingCount > 0)
                }
            }

            item {
                OutlinedButton(
                    onClick = viewModel::syncNow,
                    enabled = !uiState.isSyncing,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)
                ) {
                    if (uiState.isSyncing) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Icon(Icons.Default.CloudSync, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Sincronizar ahora", fontWeight = FontWeight.Bold)
                }
                uiState.message?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
                }
            }

            if (uiState.accessDenied || uiState.forms.isEmpty()) {
                item {
                    EmptyAccessCard(uiState.error ?: "No hay formularios disponibles.") {
                        viewModel.load(forceRefresh = true)
                    }
                }
            } else {
                item {
                    Text("Elegí el formulario", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("La selección queda lista hasta que elijas otra.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(uiState.forms, key = { it.id }) { form ->
                    val selected = uiState.selectedFormId == form.id
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.selectForm(form.id) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceContainerLowest),
                        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selected, onClick = { viewModel.selectForm(form.id) })
                            Column(Modifier.weight(1f).padding(start = 6.dp)) {
                                Text(form.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                if (form.description.isNotBlank()) Text(form.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text("${form.fields.count { it.type != "section" }} campos", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 5.dp))
                            }
                        }
                    }
                }
            }

            if (uiState.submissions.isNotEmpty()) {
                item {
                    HorizontalDivider(Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Registros recientes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        androidx.compose.material3.TextButton(onClick = onViewAllSubmissions) { Text("Ver todos") }
                    }
                }
                items(uiState.submissions.take(6), key = { it.localId }) { submission ->
                    RecentSubmissionCard(submission, onEditSubmission)
                }
            }
        }
    }
}

@Composable
private fun StatusSummaryCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, warning: Boolean = false) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (warning) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceContainerLowest)) {
        Column(Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = if (warning) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 5.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun RecentSubmissionCard(submission: OfflineSubmission, onEdit: (String, String) -> Unit) {
    val (label, color, icon) = when (submission.status) {
        SubmissionSyncStatus.SENT -> Triple("Enviado", MaterialTheme.colorScheme.primary, Icons.Default.CheckCircle)
        SubmissionSyncStatus.ERROR -> Triple("Error", MaterialTheme.colorScheme.error, Icons.Default.Error)
        SubmissionSyncStatus.SENDING -> Triple("Enviando", MaterialTheme.colorScheme.primary, Icons.Default.CloudSync)
        SubmissionSyncStatus.PENDING -> Triple("Pendiente", MaterialTheme.colorScheme.tertiary, Icons.Default.Schedule)
    }
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = color.copy(alpha = 0.12f), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(21.dp)) }
            }
            Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                Text(submission.formTitle, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(label, style = MaterialTheme.typography.labelMedium, color = color)
            }
            androidx.compose.material3.IconButton(
                onClick = { onEdit(submission.formId, submission.localId) },
                enabled = submission.status != SubmissionSyncStatus.SENDING
            ) { Icon(Icons.Default.Edit, contentDescription = "Editar registro", tint = MaterialTheme.colorScheme.primary) }
        }
    }
}

@Composable
private fun EmptyAccessCard(message: String, onRetry: () -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(36.dp))
            Text(message, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, modifier = Modifier.padding(vertical = 10.dp))
            OutlinedButton(onClick = onRetry) {
                Icon(Icons.Default.Refresh, null)
                Spacer(Modifier.size(6.dp))
                Text("Actualizar permisos")
            }
        }
    }
}
