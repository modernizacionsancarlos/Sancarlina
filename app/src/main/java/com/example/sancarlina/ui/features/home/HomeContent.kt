package com.example.sancarlina.ui.features.home

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sancarlina.ui.theme.SancarlinaAccent
import com.example.sancarlina.ui.theme.SancarlinaBackground
import com.example.sancarlina.ui.theme.SancarlinaPrimary
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeContent(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToCategory: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    var showLoginPrompt by remember { mutableStateOf(false) }

    Column(
// ... inside Column
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar - Custom Header
        HeaderSection(
            onMenuClick = { Toast.makeText(context, "Menú", Toast.LENGTH_SHORT).show() },
            onSearchClick = { Toast.makeText(context, "Buscador", Toast.LENGTH_SHORT).show() }
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Carousel Section
            BannerCarousel(uiState.banners) { banner ->
                Toast.makeText(context, "Banner: ${banner.title}", Toast.LENGTH_SHORT).show()
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Categories Section
            CategoriesGrid(uiState.categories) { category ->
                onNavigateToCategory(category.name)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Nearby Section
            NearbySection(
                product = uiState.nearbyProduct,
                onClick = { product -> onNavigateToDetail(product.id) },
                onWhatsAppClick = { product ->
                    if (auth.currentUser == null) {
                        showLoginPrompt = true
                    } else {
                        val url = "https://api.whatsapp.com/send?phone=${product.phone}&text=Hola, estoy interesado en ${product.name} visto en Sancarlina."
                        try {
                            val whatsappIntent = Intent(Intent.ACTION_VIEW, url.toUri())
                            context.startActivity(whatsappIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "WhatsApp no instalado", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }

    if (showLoginPrompt) {
        AlertDialog(
            onDismissRequest = { showLoginPrompt = false },
            title = { Text("¡Acción exclusiva!") },
            text = { Text("Para contactar a un comercio y sumar puntos, necesitas iniciar sesión.") },
            confirmButton = {
                Button(
                    onClick = { 
                        showLoginPrompt = false
                        onNavigateToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent)
                ) {
                    Text("INICIAR SESIÓN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginPrompt = false }) {
                    Text("CONTINUAR EXPLORANDO", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun HeaderSection(onMenuClick: () -> Unit, onSearchClick: () -> Unit) {
    Surface(
        color = SancarlinaPrimary,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                }
                Text(
                    text = "SANCARLINA",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                }
            }
            
            Text(
                text = "Lo nuestro, a un clic de distancia",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Search Bar Placeholder
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(CircleShape)
                    .clickable { onSearchClick() },
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Buscar productos, servicios o comercios...",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun BannerCarousel(banners: List<BannerItem>, onBannerClick: (BannerItem) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { banners.size })

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onBannerClick(banners[pagerState.currentPage]) }
        ) { page ->
            val banner = banners[page]
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(SancarlinaAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${banner.title}\n${banner.subtitle}",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                ) {
                    AsyncImage(
                        model = banner.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.Center) {
            repeat(banners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) SancarlinaPrimary else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
fun CategoriesGrid(categories: List<CategoryItem>, onCategoryClick: (CategoryItem) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val rows = categories.chunked(3)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { category ->
                    CategoryItemView(category, modifier = Modifier.weight(1f)) {
                        onCategoryClick(category)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CategoryItemView(category: CategoryItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = Color(0xFFF0F2E1),
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    tint = SancarlinaAccent,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun NearbySection(
    product: ProductItem?, 
    onClick: (ProductItem) -> Unit,
    onWhatsAppClick: (ProductItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LO QUE TENÉS CERCA",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextButton(onClick = { }) {
                Text("Filtrada", color = SancarlinaPrimary, style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (product != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(product) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF0F2E1)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = SancarlinaAccent,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Verified, 
                                    contentDescription = null, 
                                    tint = Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Sello de Origen",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    
                    Button(
                        onClick = { onWhatsAppClick(product) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp, topStart = 0.dp, topEnd = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "CONTACTO DIRECTO POR WHATSAPP",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
