package com.example.sancarlina.ui.features.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.sancarlina.ui.theme.WorkSans

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar - Custom Header
        HeaderSection()

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Carousel Section
            BannerCarousel(uiState.banners)

            Spacer(modifier = Modifier.height(32.dp))

            // Categories Section
            CategoriesGrid(uiState.categories)

            Spacer(modifier = Modifier.height(32.dp))

            // Nearby Section
            NearbySection(uiState.nearbyProduct) { product ->
                val url = "https://api.whatsapp.com/send?phone=${product.phone}&text=Hola, estoy interesado en ${product.name} visto en Sancarlina."
                val whatsappIntent = Intent(Intent.ACTION_VIEW, url.toUri())
                context.startActivity(whatsappIntent)
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Surface(
        color = SancarlinaPrimary,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SANCARLINA",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "Lo nuestro, a un clic de distancia",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Search Bar
            TextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar productos, servicios o comercios...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(CircleShape),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
        }
    }
}

@Composable
fun BannerCarousel(banners: List<BannerItem>) {
    val pagerState = rememberPagerState(pageCount = { banners.size })

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
        ) { page ->
            val banner = banners[page]
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Part (Accent Color)
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
                // Right Part (Image)
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

        // Dots Indicator
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
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
fun CategoriesGrid(categories: List<CategoryItem>) {
    // We use a fixed height container for the grid to work inside scrollable column
    // or just a non-lazy version for small fixed grids.
    Column(modifier = Modifier.fillMaxWidth()) {
        val rows = categories.chunked(3)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { category ->
                    CategoryItemView(category, modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CategoryItemView(category: CategoryItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = Color(0xFFF0F2E1), // inverse-on-surface from design
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Here we would map iconName to actual icons. 
                // For now placeholder icon.
                Icon(
                    imageVector = Icons.Default.Verified, // Placeholder
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
fun NearbySection(product: ProductItem?, onWhatsAppClick: (ProductItem) -> Unit) {
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
                modifier = Modifier.fillMaxWidth(),
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
                                imageVector = Icons.Default.Verified, // Placeholder for jar
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
                        // Placeholder for WhatsApp icon
                        Icon(Icons.Default.Verified, contentDescription = null)
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
