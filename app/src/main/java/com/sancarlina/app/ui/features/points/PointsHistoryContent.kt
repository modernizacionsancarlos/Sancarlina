package com.sancarlina.app.ui.features.points

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.features.points.components.PointsHistoryEmptyState
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.data.models.PointMovement
import com.sancarlina.app.viewmodel.PointsHistoryViewModel

@Composable
fun PointsHistoryContent(
    viewModel: PointsHistoryViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMovements()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
    ) {
        SancarlinaTopBar(
            title = stringResource(R.string.points_history_title),
            onBack = onBack
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            }
            uiState.movements.isEmpty() -> {
                PointsHistoryEmptyState()
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Actividad reciente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SancarlinaOnSurface,
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )
                    }
                    items(uiState.movements, key = { it.id }) { movement ->
                        MovementCard(movement)
                    }
                    item { Spacer(Modifier.height(88.dp)) }
                }
            }
        }
    }
}

@Composable
fun MovementCard(movement: PointMovement) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SancarlinaSurfaceContainerLowest,
        shape = SancarlinaCardShape,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (movement.isEarned) {
                    SancarlinaPrimary.copy(alpha = 0.12f)
                } else {
                    SancarlinaSecondary.copy(alpha = 0.12f)
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (movement.isEarned) {
                            Icons.Default.AddCircleOutline
                        } else {
                            Icons.Default.RemoveCircleOutline
                        },
                        contentDescription = null,
                        tint = if (movement.isEarned) SancarlinaPrimary else SancarlinaSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movement.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = SancarlinaOnSurface
                )
                Text(
                    text = movement.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = SancarlinaOutline
                )
            }

            Text(
                text = "${if (movement.amount > 0) "+" else ""}${movement.amount}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (movement.isEarned) SancarlinaPrimary else SancarlinaSecondary
            )
        }
    }
}
