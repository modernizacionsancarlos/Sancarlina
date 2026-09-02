package com.sancarlina.app.ui.features.admin.modules

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.data.repository.AdminReview
import com.sancarlina.app.ui.components.SancarlinaCard
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.admin.AdminReviewsViewModel

@Composable
fun AdminReviewsScreen(
    viewModel: AdminReviewsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        SancarlinaTopBar(title = "Moderación de reseñas", onBack = onBack)
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            uiState.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(uiState.error.orEmpty(), color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = viewModel::load) { Text("Reintentar") }
                }
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    val pending = uiState.reviews.count { it.status == "pending" }
                    Text(
                        text = if (pending == 1) "1 reseña pendiente" else "$pending reseñas pendientes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(uiState.reviews, key = { it.id }) { review ->
                    AdminReviewCard(
                        review = review,
                        processing = uiState.processingId == review.id,
                        onApprove = { viewModel.moderate(review.id, "approved") },
                        onReject = { viewModel.moderate(review.id, "rejected") }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminReviewCard(
    review: AdminReview,
    processing: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    SancarlinaCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(review.tenantName, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(review.userName, style = MaterialTheme.typography.bodySmall)
                        if (review.verifiedVisit) {
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, "Visita verificada", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Surface(
                    color = when (review.status) {
                        "approved" -> MaterialTheme.colorScheme.primaryContainer
                        "rejected" -> MaterialTheme.colorScheme.errorContainer
                        else -> Color(0xFFFFE7A7)
                    },
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = when (review.status) {
                            "approved" -> "Aprobada"
                            "rejected" -> "Rechazada"
                            else -> "Pendiente"
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(review.rating) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFF2B32A), modifier = Modifier.size(18.dp))
                }
            }
            if (review.comment.isNotBlank()) {
                Text(review.comment, style = MaterialTheme.typography.bodyMedium)
            }
            if (review.status == "pending") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onReject, enabled = !processing, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Close, null)
                        Spacer(Modifier.width(5.dp))
                        Text("Rechazar")
                    }
                    Button(onClick = onApprove, enabled = !processing, modifier = Modifier.weight(1f)) {
                        if (processing) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Check, null)
                            Spacer(Modifier.width(5.dp))
                            Text("Aprobar")
                        }
                    }
                }
            }
        }
    }
}
