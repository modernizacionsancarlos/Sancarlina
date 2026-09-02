package com.sancarlina.app.ui.features.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.data.models.OfflineSubmission
import com.sancarlina.app.data.models.SubmissionSyncStatus
import com.sancarlina.app.ui.components.SancarlinaElevatedCard
import com.sancarlina.app.ui.components.SancarlinaTopBar
import java.text.DateFormat
import java.util.Date

@Composable
fun PendingSubmissionsContent(
    viewModel: PendingSubmissionsViewModel,
    onBack: () -> Unit,
    onEditSubmission: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { SancarlinaTopBar(title = "Envíos de formularios", onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = if (uiState.pendingCount == 0) {
                    "Todos los formularios están sincronizados."
                } else {
                    "${uiState.pendingCount} formulario(s) requieren sincronización."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = viewModel::syncNow,
                enabled = !uiState.isSyncing,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (uiState.isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Sync, contentDescription = null)
                }
                Text(
                    text = if (uiState.isSyncing) "Sincronizando…" else "Sincronizar ahora",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            uiState.message?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            if (uiState.submissions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CloudOff, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(10.dp))
                    Text("Todavía no hay envíos guardados en este dispositivo.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.submissions, key = { it.localId }) { submission ->
                        SubmissionCard(
                            submission = submission,
                            onRetry = { viewModel.retry(submission.localId) },
                            onEdit = { onEditSubmission(submission.formId, submission.localId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubmissionCard(submission: OfflineSubmission, onRetry: () -> Unit, onEdit: () -> Unit) {
    val presentation = statusPresentation(submission.status)
    SancarlinaElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = submission.formTitle.ifBlank { "Formulario ${submission.formId}" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        .format(Date(submission.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = presentation.color.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = presentation.icon,
                        contentDescription = null,
                        tint = presentation.color,
                        modifier = Modifier.height(16.dp)
                    )
                    Text(
                        text = presentation.label,
                        color = presentation.color,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        Text(
            text = "${submission.data.size} dato(s) completado(s)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
        submission.lastError?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onEdit,
                enabled = submission.status != SubmissionSyncStatus.SENDING,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Text("Editar", modifier = Modifier.padding(start = 6.dp))
            }
            if (submission.status == SubmissionSyncStatus.ERROR) {
                OutlinedButton(onClick = onRetry, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Text("Reintentar", modifier = Modifier.padding(start = 6.dp))
                }
            }
        }
    }
}

private data class StatusPresentation(
    val label: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
private fun statusPresentation(status: SubmissionSyncStatus): StatusPresentation = when (status) {
    SubmissionSyncStatus.PENDING -> StatusPresentation("Pendiente", Icons.Default.CloudOff, Color(0xFF9A6700))
    SubmissionSyncStatus.SENDING -> StatusPresentation("Enviando", Icons.Default.Sync, MaterialTheme.colorScheme.primary)
    SubmissionSyncStatus.SENT -> StatusPresentation("Enviado", Icons.Default.CheckCircle, Color(0xFF257A3E))
    SubmissionSyncStatus.ERROR -> StatusPresentation("Error", Icons.Default.Error, MaterialTheme.colorScheme.error)
}
