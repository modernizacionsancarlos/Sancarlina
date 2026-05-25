package com.example.sancarlina.ui.features.points

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sancarlina.ui.theme.SancarlinaAccent
import com.example.sancarlina.ui.theme.SancarlinaBackground
import com.example.sancarlina.ui.theme.SancarlinaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsContent(viewModel: PointsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SANCARLINA",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Menu */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SancarlinaPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SancarlinaBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Mis Puntos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Acumulá y disfrutá experiencias exclusivas.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Hero Points Card
            BalanceCard(uiState.balance)

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Action Button
            Button(
                onClick = { viewModel.startQrGeneration() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent),
                shape = RoundedCornerShape(28.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("GENERAR QR DE CANJE", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Redeem Benefits Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Canjear Beneficios",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "VER TODOS >",
                    style = MaterialTheme.typography.labelSmall,
                    color = SancarlinaPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            uiState.benefits.forEach { benefit ->
                BenefitCard(benefit) {
                    viewModel.onBenefitClick(benefit)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // QR Dialog [VISTA 8]
    if (uiState.qrCodeActive) {
        QrDialog(
            timeRemaining = uiState.qrTimeRemaining,
            onDismiss = { viewModel.dismissModal() },
            onSimulateScan = { viewModel.simulateSuccessfulScan(100) }
        )
    }

    // Redemption Confirmation Dialog
    if (uiState.selectedBenefit != null) {
        val benefit = uiState.selectedBenefit!!
        AlertDialog(
            onDismissRequest = { viewModel.dismissModal() },
            confirmButton = {
                Button(
                    onClick = { viewModel.redeemBenefit() },
                    colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary)
                ) {
                    Text("CANJEAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissModal() }) {
                    Text("CANCELAR", color = Color.Gray)
                }
            },
            title = { Text("Confirmar Canje") },
            text = { Text("¿Deseas canjear '${benefit.title}' por ${benefit.cost} puntos?") },
            containerColor = Color.White
        )
    }

    // Success Modal [VISTA 14/33]
    if (uiState.showSuccessModal) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissModal() },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissModal() },
                    colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary)
                ) {
                    Text("GENIAL!")
                }
            },
            title = { Text("¡Canje Exitoso!") },
            text = { Text("Tu beneficio ha sido procesado correctamente. ¡Disfrutalo!") },
            containerColor = Color.White
        )
    }
}

@Composable
fun BalanceCard(balance: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SancarlinaPrimary,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Decorative elements
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .offset(x = 250.dp, y = (-50).dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Stars, 
                            contentDescription = null, 
                            tint = Color(0xFFD8EB98),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "SALDO ACTUAL",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFD8EB98),
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = balance.toString(),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "pts",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BenefitCard(benefit: BenefitItem, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = benefit.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = SancarlinaBackground,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = benefit.category,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
                
                Text(
                    text = benefit.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = benefit.brand,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Stars, 
                        contentDescription = null, 
                        tint = SancarlinaPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${benefit.cost} pts",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun QrDialog(
    timeRemaining: String,
    onDismiss: () -> Unit,
    onSimulateScan: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {},
        title = { 
            Text(
                "QR de Canje", 
                modifier = Modifier.fillMaxWidth(), 
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Mostrá este código al comerciante",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // QR Placeholder
                Surface(
                    modifier = Modifier
                        .size(200.dp)
                        .clickable { onSimulateScan() },
                    color = Color.White,
                    border = BorderStroke(1.dp, Color.LightGray),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.QrCode2, 
                            contentDescription = null, 
                            modifier = Modifier.size(180.dp),
                            tint = Color.Black
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Vence en: $timeRemaining",
                    style = MaterialTheme.typography.titleMedium,
                    color = SancarlinaAccent,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = { onDismiss() }) {
                    Text("CERRAR", color = Color.Gray)
                }
            }
        },
        containerColor = Color.White
    )
}
