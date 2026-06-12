package com.sancarlina.app.ui.features.product

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sancarlina.app.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.sancarlina.app.ui.theme.*

@Composable
fun ProductDetailContent(
    productId: String,
    viewModel: ProductDetailViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var isFav by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    Box(modifier = Modifier.fillMaxSize().background(SancarlinaSurface)) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SancarlinaPrimary)
            }
        } else {
            val product = uiState.product ?: return@Box

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero Image
                Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                    startY = 0f
                                )
                            )
                    )
                }

                // Content Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-32).dp),
                    color = SancarlinaSurfaceContainerLowest,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        // Badge & Title Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = SancarlinaPrimary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Verified, null, tint = SancarlinaPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("SELLO DE ORIGEN", style = MaterialTheme.typography.labelSmall, color = SancarlinaPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            IconButton(onClick = { isFav = !isFav }) {
                                Icon(
                                    if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    stringResource(R.string.cd_favorite),
                                    tint = SancarlinaSecondary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = SancarlinaOnSurface
                        )
                        
                        Text(
                            text = product.location,
                            style = MaterialTheme.typography.titleMedium,
                            color = SancarlinaOnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(Modifier.height(24.dp))

                        // Price and Main Action
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = product.price,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = SancarlinaPrimary
                            )
                            
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = Uri.parse("https://wa.me/${product.phone}")
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaSecondary)
                            ) {
                                Icon(Icons.Default.Chat, null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("CONSULTAR")
                            }
                        }

                        Spacer(Modifier.height(32.dp))

                        Text(
                            "Descripción",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SancarlinaOnSurface
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = SancarlinaOnSurfaceVariant,
                            lineHeight = 24.sp
                        )

                        if (product.galleryImages.isNotEmpty()) {
                            Spacer(Modifier.height(32.dp))
                            Text(
                                "Galería de fotos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SancarlinaOnSurface
                            )
                            Spacer(Modifier.height(16.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(product.galleryImages) { img ->
                                    AsyncImage(
                                        model = img,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(140.dp)
                                            .clip(RoundedCornerShape(20.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(64.dp))
                    }
                }
            }
        }

        // Floating Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_back), tint = Color.White)
        }
    }
}
