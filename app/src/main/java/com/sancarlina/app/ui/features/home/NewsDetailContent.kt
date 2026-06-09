package com.sancarlina.app.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sancarlina.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailContent(onBack: () -> Unit, onNavigateToMap: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaSurface)
    ) {
        // Hero Image Background (Top Half)
        AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDWqyi8NzCtRaNvBXZ6-ipds6pnMxpGIQIIMNwEewvq62vyTz7I_npHR5I1OmeghodCKztReehm7wi6mTSsC6nNLlkRrGISvIaDXhL_1brjOzBcmcko3mprzhLAn1U5Q8TrL4xNPqGFdZT8z0ebEhh8_sc67_nbMZnzyOL3jv98oDlFwaAhdPf4RXv8buOxu2qdLsbzVai2_nBPZi-E-kEwcQun2ahmPDFuwkakqL30lHpFv5mUHKygadFK0lCLXGfAPDjikTxknpM",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        // Gradient Overlay for Back Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(260.dp))

            // Main Details Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                color = SancarlinaSurfaceContainerLowest,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Badge
                    Surface(
                        color = SancarlinaPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "EVENTO REGIONAL",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = SancarlinaPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Feria de Productores Locales",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DetailInfoItem(Icons.Default.CalendarToday, "Sábado y Domingo", Modifier.weight(1f))
                        DetailInfoItem(Icons.Default.Schedule, "10:00 a 18:00 hs", Modifier.weight(1f))
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailInfoItem(Icons.Default.LocationOn, "Plaza de San Carlos", Modifier.fillMaxWidth())
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        text = "Sobre el evento",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Acompáñenos a disfrutar de la mejor selección de productos locales. En esta edición de la feria, encontrará verduras orgánicas recién cosechadas, vinos maduros de la región, mermeladas artesanales y artesanías en madera.\n\nUna oportunidad perfecta para apoyar a nuestros productores y llevar a casa sabores auténticos en un ambiente familiar y relajado.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SancarlinaOnSurfaceVariant,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Action Button
                    Button(
                        onClick = onNavigateToMap,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SancarlinaSecondary)
                    ) {
                        Icon(Icons.Default.Map, null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Ver ubicación en el mapa", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        // Back Button (Floating)
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }
    }
}

@Composable
fun DetailInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = SancarlinaSurfaceContainerLow,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = SancarlinaPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = SancarlinaOnSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
