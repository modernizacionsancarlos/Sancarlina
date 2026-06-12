package com.sancarlina.app.ui.features.servicios

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sancarlina.app.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sancarlina.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosSelloContent(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(SancarlinaSurface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SancarlinaSurfaceContainerLow
            ) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_back), tint = SancarlinaPrimary)
                    }
                    Text(
                        text = "Servicios y Sello",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero Section
                Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDBlnBzECUfV3Q8L9JVdnEfGXXRXrm9ZfJJ0m6tqX_9A7EchP_mYE96WOuNQsAK2rDez5ls2ZSOBgbO_O0Q-6C8z2mPntKysTbXVeoEAWu5DOP0ALbMWzGu0HhWe34w0RAPmCyOD9lsSuyWoHUOd6FY_Q3uuFxqXkSrmx7q9kczsPQcXY4f4gCsMlepsiSSQILCTM50_OL5UBRHHhVQhXNLGqBWD0GcjVj0skE_S3r06yEUjpBUgKDMA4b2McArh82DvsEZb-1EjWs",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Sello de Origen", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Identidad y Calidad Sancarlina", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
                    }
                }

                Column(modifier = Modifier.padding(24.dp)) {
                    // Feature Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SancarlinaSurfaceContainerLowest,
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = CircleShape,
                                    color = SancarlinaSecondary.copy(alpha = 0.1f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.WorkspacePremium, null, tint = SancarlinaSecondary)
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("Garantía Municipal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "El Sello de Origen certifica que los productos han sido elaborados bajo procesos artesanales y estándares de calidad en el departamento de San Carlos.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = SancarlinaOnSurfaceVariant,
                                lineHeight = 24.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text("Rubros Destacados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SancarlinaOnSurface)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                        ServiceBadge("Vinos", Icons.Default.WineBar, Modifier.weight(1f))
                        ServiceBadge("Miel", Icons.Default.FilterVintage, Modifier.weight(1f))
                        ServiceBadge("Artesanías", Icons.Default.Palette, Modifier.weight(1f))
                    }
                    
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
    }
}

@Composable
fun ServiceBadge(name: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = SancarlinaSurfaceContainerLow,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = SancarlinaPrimary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}
