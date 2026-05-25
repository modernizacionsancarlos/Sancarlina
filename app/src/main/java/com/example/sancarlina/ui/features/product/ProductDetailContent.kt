package com.example.sancarlina.ui.features.product

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sancarlina.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailContent(
    productId: String,
    viewModel: ProductDetailViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

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
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { Toast.makeText(context, "Buscador", Toast.LENGTH_SHORT).show() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SancarlinaPrimary
                )
            )
        },
        bottomBar = {
            uiState.product?.let { product ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = {
                            val url = "https://wa.me/${product.phone}?text=Hola, estoy interesado en ${product.name}"
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                            } catch (e: Exception) {
                                Toast.makeText(context, "WhatsApp no instalado", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CONTACTO DIRECTO POR WHATSAPP", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        uiState.product?.let { product ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(SancarlinaBackground)
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero Image
                Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, SancarlinaBackground)
                                )
                            )
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .offset(y = (-20).dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.size(48.dp)
                        ) {
                            IconButton(onClick = { viewModel.toggleFavorite() }) {
                                Icon(
                                    if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (product.isFavorite) SancarlinaAccent else Color.Gray
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = product.location, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.tags.forEach { tag ->
                            Surface(
                                color = if (tag == "Artesanal") SancarlinaPrimary.copy(alpha = 0.1f) else Color.White,
                                shape = CircleShape,
                                border = if (tag != "Artesanal") androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray) else null
                            ) {
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (tag == "Artesanal") SancarlinaPrimary else Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Origen y Elaboración",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 24.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Galería",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(product.galleryImages) { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(112.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.LightGray),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(100.dp)) // Extra space for bottom bar
            }
        }
    }
}
