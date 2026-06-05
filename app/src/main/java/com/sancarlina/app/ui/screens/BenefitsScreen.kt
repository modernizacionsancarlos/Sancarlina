package com.sancarlina.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.sancarlina.app.R
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.PointsViewModel
import com.sancarlina.app.viewmodel.BenefitItem
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenefitsScreen(
    viewModel: PointsViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onNavigateToScanner: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser = auth.currentUser

    if (currentUser == null) {
        AuthRequiredPlaceholder(onNavigateToLogin, onOpenDrawer)
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mis Puntos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToScanner) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear", tint = Color.White)
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
        ) {
            // Points Header Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                color = SancarlinaPrimary,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Tus puntos acumulados",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        "${uiState.balance}",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.startQrGeneration() },
                        colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.QrCode, null)
                        Spacer(Modifier.width(8.dp))
                        Text("GENERAR MI QR", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                "BENEFICIOS EXCLUSIVOS",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                letterSpacing = 1.sp
            )

            // Benefits List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.benefits) { benefit ->
                    BenefitCard(benefit) {
                        viewModel.onBenefitClick(benefit)
                    }
                }
            }
        }
    }

    // Modal de QR
    if (uiState.qrCodeActive) {
        QrModal(
            timeRemaining = uiState.qrTimeRemaining,
            onDismiss = { viewModel.dismissModal() }
        )
    }

    // Modal de Canje
    uiState.selectedBenefit?.let { benefit ->
        RedeemDialog(
            benefit = benefit,
            userBalance = uiState.balance,
            onDismiss = { viewModel.dismissModal() },
            onRedeem = { viewModel.redeemBenefit() }
        )
    }
    
    // Success Modal
    if (uiState.showSuccessModal) {
        SuccessPointsDialog(onDismiss = { viewModel.dismissModal() })
    }
}

@Composable
fun BenefitCard(benefit: BenefitItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = benefit.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    benefit.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    benefit.brand,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = SancarlinaAccent.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "${benefit.cost} PTS",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = SancarlinaAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QrModal(timeRemaining: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Muestra este QR al comercio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Placeholder for real QR
                Surface(
                    modifier = Modifier.size(240.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with real QR generator later
                        contentDescription = "QR Code",
                        modifier = Modifier.fillMaxSize().padding(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    "Expira en: $timeRemaining",
                    style = MaterialTheme.typography.labelLarge,
                    color = SancarlinaAccent,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                TextButton(onClick = onDismiss) {
                    Text("CERRAR", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun RedeemDialog(benefit: BenefitItem, userBalance: Int, onDismiss: () -> Unit, onRedeem: () -> Unit) {
    val canAfford = userBalance >= benefit.cost
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Canjear Beneficio") },
        text = {
            Column {
                Text("¿Estás seguro que quieres canjear este beneficio?")
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(benefit.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("${benefit.cost} PTS", color = SancarlinaAccent, fontWeight = FontWeight.Bold)
                }
                if (!canAfford) {
                    Text(
                        "No tienes suficientes puntos acumulados.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onRedeem,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent)
            ) {
                Text("CANJEAR AHORA")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR", color = Color.Gray)
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun SuccessPointsDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = Color(0xFFF0F2E1),
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle, 
                        contentDescription = null, 
                        tint = SancarlinaPrimary,
                        modifier = Modifier.padding(16.dp).size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "¡Operación Exitosa!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Tus puntos han sido actualizados correctamente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary)
                ) {
                    Text("ENTENDIDO")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthRequiredPlaceholder(onNavigateToLogin: () -> Unit, onOpenDrawer: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mis Puntos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = SancarlinaPrimary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SancarlinaBackground)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Inicia sesión para ver tus puntos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = SancarlinaPrimary
            )
            Text(
                "Necesitas estar registrado para sumar puntos con tus compras y canjear beneficios exclusivos.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("INICIAR SESIÓN AHORA", fontWeight = FontWeight.Bold)
            }
        }
    }
}
