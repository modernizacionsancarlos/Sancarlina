package com.example.sancarlina.ui.features.points

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sancarlina.ui.theme.SancarlinaAccent
import com.example.sancarlina.ui.theme.SancarlinaBackground
import com.example.sancarlina.ui.theme.SancarlinaPrimary

@Composable
fun PointsContent(viewModel: PointsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = "Mis Puntos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Acumulá y disfrutá experiencias exclusivas.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Balance Card
        BalanceCard(uiState.balance)

        Spacer(modifier = Modifier.height(24.dp))

        // QR Button
        Button(
            onClick = { viewModel.startQrGeneration() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent),
            shape = CircleShape
        ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("GENERAR QR DE CANJE", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Benefits Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Canjear Beneficios",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { }) {
                Text("Ver todos", color = SancarlinaPrimary)
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SancarlinaPrimary, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        uiState.benefits.forEach { benefit ->
            BenefitCard(benefit)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }

    // QR Dialog
    if (uiState.qrCodeActive) {
        QrDialog(
            timeRemaining = uiState.qrTimeRemaining,
            onDismiss = { /* Action to close if needed */ },
            onSimulateScan = { viewModel.simulateSuccessfulScan(500) }
        )
    }

    // Success Modal
    if (uiState.showSuccessModal) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissModal() },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissModal() }) {
                    Text("ENTENDIDO", color = SancarlinaAccent, fontWeight = FontWeight.Bold)
                }
            },
            title = { Text("¡Canje Exitoso!", fontWeight = FontWeight.Bold) },
            text = { Text("Tu beneficio ha sido procesado correctamente. ¡Que lo disfrutes!") },
            containerColor = Color.White
        )
    }
}

@Composable
fun BalanceCard(balance: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        color = SancarlinaPrimary,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(32.dp)
                    )
                }
                Text(
                    text = "SALDO ACTUAL",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 2.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = balance.toString(),
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " pts",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BenefitCard(benefit: BenefitItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = benefit.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .size(86.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 12.dp)
            ) {
                Surface(
                    color = SancarlinaBackground,
                    shape = CircleShape
                ) {
                    Text(
                        text = benefit.category,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = benefit.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = benefit.brand,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = SancarlinaPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${benefit.cost} pts",
                        color = SancarlinaPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QrDialog(timeRemaining: String, onDismiss: () -> Unit, onSimulateScan: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onSimulateScan,
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("SIMULAR ESCANEO (PRUEBA)")
            }
        },
        title = { 
            Text(
                "QR DE CANJE", 
                modifier = Modifier.fillMaxWidth(), 
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                // Mock QR Placeholder
                Surface(
                    modifier = Modifier.size(200.dp),
                    color = SancarlinaBackground,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(120.dp), tint = Color.DarkGray)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "El código expira en:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = timeRemaining,
                    style = MaterialTheme.typography.headlineMedium,
                    color = SancarlinaAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = Color.White
    )
}
