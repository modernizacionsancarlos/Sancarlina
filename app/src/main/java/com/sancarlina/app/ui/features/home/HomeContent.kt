package com.sancarlina.app.ui.features.home

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.WineBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.HomeViewModel
import com.sancarlina.app.viewmodel.HomeUiState
import com.sancarlina.app.viewmodel.BannerItem
import com.sancarlina.app.viewmodel.CategoryItem

@Composable
fun HomeContent(
    viewModel: HomeViewModel = viewModel(),
    uiStateOverride: HomeUiState? = null,
    onNavigateToCategory: (String) -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToNews: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onOpenDrawer: () -> Unit = {}
) {
    val collectedState by viewModel.uiState.collectAsState()
    val uiState = uiStateOverride ?: collectedState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar [Target Style from assets]
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .clickable { onNavigateToSearch() },
            color = SancarlinaSurfaceContainer
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    stringResource(R.string.cd_search),
                    tint = SancarlinaOutline,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "¿Qué estás buscando?",
                    color = SancarlinaOutline,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Ofertas y Novedades
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Ofertas y Novedades",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = SancarlinaOnSurface
            )
            Text(
                "Ver todo",
                color = SancarlinaPrimary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onNavigateToNews() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(uiState.banners) { banner ->
                BannerCard(banner)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            stringResource(R.string.home_explore_section),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            color = SancarlinaOnSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        // Grid 2 Columns [Target Style]
        val categories = uiState.categories
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            for (i in categories.indices step 2) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CategoryBentoCard(categories[i], modifier = Modifier.weight(1f)) { onNavigateToCategory(categories[i].name) }
                    if (i + 1 < categories.size) {
                        CategoryBentoCard(categories[i+1], modifier = Modifier.weight(1f)) { onNavigateToCategory(categories[i+1].name) }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom bar
    }
}

@Composable
fun BannerCard(banner: BannerItem) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = banner.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 250f
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                // Badge
                Surface(
                    color = SancarlinaPrimary.copy(alpha = 0.9f),
                    shape = CircleShape
                ) {
                    Text(
                        text = if (banner.title.contains("OFERTA", true)) "-20% Dto" else banner.title,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = banner.subtitle.ifEmpty { banner.title },
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 28.sp
                )
            }
        }
    }
}

@Composable
fun CategoryBentoCard(category: CategoryItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = SancarlinaSurfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, SancarlinaOutlineVariant.copy(alpha = 0.3f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                color = SancarlinaPrimaryContainer.copy(alpha = 0.2f),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val icon = when {
                        category.name.contains("BODEGA", true) -> Icons.Default.WineBar
                        category.name.contains("GASTRONOMIA", true) -> Icons.Default.Restaurant
                        category.name.contains("ARTESANIA", true) -> Icons.Default.Palette
                        category.name.contains("ALOJAMIENTO", true) -> Icons.Default.Bed
                        else -> Icons.Default.Category
                    }
                    Icon(icon, null, tint = SancarlinaPrimary, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = category.name.lowercase().capitalizeWords(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = SancarlinaOnSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }
