package com.example.sancarlina.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
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
import com.example.sancarlina.ui.theme.SancarlinaAccent
import com.example.sancarlina.ui.theme.SancarlinaBackground
import com.example.sancarlina.ui.theme.SancarlinaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailContent(onBack: () -> Unit, onNavigateToMap: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "NOVEDADES",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = SancarlinaPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
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
                .padding(20.dp)
        ) {
            // Hero Image
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDWqyi8NzCtRaNvBXZ6-ipds6pnMxpGIQIIMNwEewvq62vyTz7I_npHR5I1OmeghodCKztReehm7wi6mTSsC6nNLlkRrGISvIaDXhL_1brjOzBcmcko3mprzhLAn1U5Q8TrL4xNPqGFdZT8z0ebEhh8_sc67_nbMZnzyOL3jv98oDlFwaAhdPf4RXv8buOxu2qdLsbzVai2_nBPZi-E-kEwcQun2ahmPDFuwkakqL30lHpFv5mUHKygadFK0lCLXGfAPDjikTxknpM",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Details Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Este fin de semana: Feria de Productores Locales",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    DetailInfoRow(Icons.Default.CalendarToday, "Sábado y Domingo")
                    DetailInfoRow(Icons.Default.Schedule, "10:00 a 18:00 hs")
                    DetailInfoRow(Icons.Default.LocationOn, "Plaza de San Carlos")
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = "Acompáñenos a disfrutar de la mejor selección de productos locales. En esta edición de la feria, encontrará verduras orgánicas recién cosechadas, vinos maduros de la región, mermeladas artesanales y artesanías en madera. Una oportunidad perfecta para apoyar a nuestros productores y llevar a casa sabores auténticos en un ambiente familiar y relajado.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNavigateToMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent)
            ) {
                Icon(Icons.Default.Map, null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("VER UBICACIÓN EN EL MAPA", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun DetailInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = SancarlinaPrimary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = SancarlinaPrimary, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
