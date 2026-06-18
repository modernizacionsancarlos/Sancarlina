package com.sancarlina.app.ui.features.points

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaCard
import com.sancarlina.app.ui.features.points.components.PointsBalanceCard
import com.sancarlina.app.ui.features.points.components.QrActionCard
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.BenefitItem
import com.sancarlina.app.viewmodel.PointsViewModel

@Composable
fun BenefitsContent(
    viewModel: PointsViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onNavigateToScanner: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        PointsBalanceCard(balance = uiState.balance)

        Spacer(modifier = Modifier.height(16.dp))

        QrActionCard(onScanClick = onNavigateToScanner)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.points_benefits_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = SancarlinaOnSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            }
            uiState.benefits.isEmpty() -> {
                SancarlinaCard {
                    Text(
                        text = stringResource(R.string.points_benefits_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SancarlinaOnSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(uiState.benefits, key = { it.id }) { benefit ->
                        BenefitCard(benefit = benefit)
                    }
                }
            }
        }
    }
}

@Composable
fun BenefitCard(benefit: BenefitItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SancarlinaSurfaceContainerLowest,
        shape = SancarlinaCardShape,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                color = SancarlinaSecondary.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ConfirmationNumber, null, tint = SancarlinaSecondary)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = benefit.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = SancarlinaOnSurface
                )
                Text(
                    text = "${benefit.cost} puntos",
                    style = MaterialTheme.typography.labelLarge,
                    color = SancarlinaPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            OutlinedButton(
                onClick = { /* canje pendiente */ },
                shape = SancarlinaChipShape
            ) {
                Text("Canjear", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
