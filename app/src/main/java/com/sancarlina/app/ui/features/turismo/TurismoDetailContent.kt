package com.sancarlina.app.ui.features.turismo
 
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.TurismoDetailViewModel
import com.sancarlina.app.viewmodel.TurismoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurismoDetailContent(
    pointId: String,
    viewModel: TurismoDetailViewModel,
    onBack: () -> Unit
) {
    val pointState by viewModel.point.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(pointId) {
        viewModel.loadPointDetails(pointId)
    }

    Scaffold(
        containerColor = SancarlinaBackground
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            }
            pointState == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = SancarlinaOutlineVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No pudimos encontrar este punto turístico",
                        style = MaterialTheme.typography.titleMedium,
                        color = SancarlinaOnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary)
                    ) {
                        Text("Volver", color = Color.White)
                    }
                }
            }
            else -> {
                val point = pointState!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header Banner Image with Floating Back Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    ) {
                        AsyncImage(
                            model = point.imageUrl,
                            contentDescription = point.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Floating Back Button
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(16.dp)
                                .size(40.dp)
                                .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = SancarlinaPrimary
                            )
                        }
                    }

                    // Content Details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Title
                        Text(
                            text = point.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = SancarlinaOnSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Category & Rating Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Category Chip
                            val categoryIcon = when (point.category.lowercase(java.util.Locale.ROOT)) {
                                "naturaleza" -> Icons.Default.Landscape
                                "cultura" -> Icons.Default.AccountBalance
                                "aventura" -> Icons.Default.Explore
                                else -> Icons.Default.Place
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SancarlinaPrimary.copy(alpha = 0.4f)),
                                color = SancarlinaPrimary.copy(alpha = 0.05f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = categoryIcon,
                                        contentDescription = null,
                                        tint = SancarlinaPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = point.category,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = SancarlinaPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Rating Capsule
                            if (point.rating > 0) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SancarlinaTertiary.copy(alpha = 0.1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = SancarlinaTertiary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${point.rating}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = SancarlinaTertiary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = SancarlinaOutlineVariant.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(20.dp))

                        // Description
                        Text(
                            text = "Acerca de este lugar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SancarlinaOnSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = point.description,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = SancarlinaOnSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Info Card (Schedule, Phone, Address)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = SancarlinaSurfaceContainerLow,
                            tonalElevation = 1.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Location Row
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = SancarlinaPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Ubicación",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = SancarlinaOnSurface
                                        )
                                        Text(
                                            text = point.location,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = SancarlinaOnSurfaceVariant
                                        )
                                    }
                                }

                                // Schedule Row
                                if (point.schedule.isNotEmpty()) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = SancarlinaPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Horario de visita",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = SancarlinaOnSurface
                                            )
                                            Text(
                                                text = point.schedule,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = SancarlinaOnSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                // Contact Phone Row
                                if (point.phone.isNotEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        modifier = Modifier.clickable {
                                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${point.phone}"))
                                            context.startActivity(dialIntent)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = null,
                                            tint = SancarlinaPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Contacto (Llamar)",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = SancarlinaOnSurface
                                            )
                                            Text(
                                                text = point.phone,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = SancarlinaPrimary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // "Cómo llegar" Button (Map Navigation Action)
                        if (point.latitude != 0.0 && point.longitude != 0.0) {
                            Button(
                                onClick = {
                                    val mapIntentUri = Uri.parse("geo:${point.latitude},${point.longitude}?q=${Uri.encode(point.name)}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, mapIntentUri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (e: Exception) {
                                        val fallbackUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${point.latitude},${point.longitude}")
                                        val fallbackIntent = Intent(Intent.ACTION_VIEW, fallbackUri)
                                        context.startActivity(fallbackIntent)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary),
                                shape = CircleShape
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Map,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Cómo llegar",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
