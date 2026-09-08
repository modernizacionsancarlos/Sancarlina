package com.sancarlina.app.ui.features.points

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Star
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

import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.BorderStroke

@Composable
fun BenefitsContent(
    viewModel: PointsViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onNavigateToScanner: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Confirmation Dialog
    uiState.selectedBenefit?.let { benefit ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelBenefitSelection() },
            title = {
                Text(
                    text = "¿Confirmás el canje?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Vas a canjear ${benefit.cost} puntos por:\n\"${benefit.title}\" en ${benefit.brand}.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Button(
                        onClick = { viewModel.redeemBenefit() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = SancarlinaChipShape
                    ) {
                        Text("Canjear", color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.cancelBenefitSelection() }
                ) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.primary)
                }
            },
            shape = SancarlinaCardShape,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    }

    // Success Dialog with Voucher Code & Mock QR
    if (uiState.showSuccessModal) {
        val voucherCode = remember { "SC-VAL-${(1000..9999).random()}" }
        AlertDialog(
            onDismissRequest = { viewModel.dismissModal() },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "¡Canje Exitoso!",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Presentá este código en el comercio para recibir tu beneficio:",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Surface(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        shape = SancarlinaCardShape,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = voucherCode,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                            letterSpacing = 1.5.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .background(Color.White, shape = SancarlinaCardShape)
                            .drawMockQr(color = MaterialTheme.colorScheme.onSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { viewModel.dismissModal() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = SancarlinaChipShape,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text("Entendido", color = Color.White)
                    }
                }
            },
            shape = SancarlinaCardShape,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    }

    // Error Dialog
    uiState.error?.let { errorMessage ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = {
                Text(
                    text = "Ocurrió un problema",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearError() }
                ) {
                    Text("Aceptar", color = MaterialTheme.colorScheme.primary)
                }
            },
            shape = SancarlinaCardShape,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "benefits_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Puntos y Beneficios",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Sumá puntos en cada compra y canjeá premios",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Beneficios",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        item(key = "balance_card") {
            PointsBalanceCard(balance = uiState.balance)
        }

        item(key = "qr_action_card") {
            QrActionCard(onScanClick = onNavigateToScanner)
        }

        item(key = "how_it_works") {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = SancarlinaCardShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "¿Cómo funciona?",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HowItWorksStep(number = "1", title = "Comprá local", desc = "En comercios adheridos")
                        HowItWorksStep(number = "2", title = "Escaneá QR", desc = "Sumá tus puntos al pagar")
                        HowItWorksStep(number = "3", title = "Canjeá", desc = "Descuentos y regalos")
                    }
                }
            }
        }

        item(key = "benefits_header") {
            Column {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.points_benefits_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (uiState.benefits.isNotEmpty()) {
                        Text(
                            text = "${uiState.benefits.size} disponibles",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        when {
            uiState.isLoading && uiState.benefits.isEmpty() -> {
                item(key = "loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            uiState.benefits.isEmpty() -> {
                item(key = "empty") {
                    SancarlinaCard {
                        Text(
                            text = stringResource(R.string.points_benefits_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }
            else -> {
                items(uiState.benefits, key = { it.id }) { benefit ->
                    BenefitCard(benefit = benefit, onRedeemClick = { viewModel.onBenefitClick(benefit) })
                }
            }
        }
    }
}

@Composable
private fun HowItWorksStep(number: String, title: String, desc: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(96.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
fun BenefitCard(benefit: BenefitItem, onRedeemClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = SancarlinaCardShape,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (benefit.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = benefit.imageUrl,
                    contentDescription = benefit.title,
                    modifier = Modifier.size(72.dp).clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(72.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.ConfirmationNumber,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (benefit.brand.isNotBlank()) {
                    Text(
                        text = benefit.brand,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                Text(
                    text = benefit.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Stars,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${benefit.cost} pts",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = onRedeemClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text("Canjear", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Custom Draw Modifier to render a premium mockup QR Code in canvas
fun Modifier.drawMockQr(color: Color) = this.drawBehind {
    val sizePx = size.width
    val cellSize = sizePx / 10f
    
    // Finder pattern 1 (Top-Left)
    drawRect(color = color, topLeft = Offset(0f, 0f), size = Size(cellSize * 3, cellSize * 3))
    drawRect(color = Color.White, topLeft = Offset(cellSize, cellSize), size = Size(cellSize, cellSize))
    
    // Finder pattern 2 (Top-Right)
    drawRect(color = color, topLeft = Offset(sizePx - cellSize * 3, 0f), size = Size(cellSize * 3, cellSize * 3))
    drawRect(color = Color.White, topLeft = Offset(sizePx - cellSize * 2, cellSize), size = Size(cellSize, cellSize))
    
    // Finder pattern 3 (Bottom-Left)
    drawRect(color = color, topLeft = Offset(0f, sizePx - cellSize * 3), size = Size(cellSize * 3, cellSize * 3))
    drawRect(color = Color.White, topLeft = Offset(cellSize, sizePx - cellSize * 2), size = Size(cellSize, cellSize))
    
    // Random QR-like pixel clusters
    val clusters = listOf(
        4 to 4, 5 to 4, 4 to 5, 6 to 6, 7 to 5, 5 to 7, 7 to 7, 6 to 3, 3 to 6,
        8 to 4, 4 to 8, 8 to 8, 9 to 6, 6 to 9, 8 to 7, 7 to 8, 9 to 9
    )
    for ((col, row) in clusters) {
        drawRect(
            color = color,
            topLeft = Offset(col * cellSize, row * cellSize),
            size = Size(cellSize, cellSize)
        )
    }
}
