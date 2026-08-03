package com.sancarlina.app.ui.features.turismo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sancarlina.app.ui.components.SancarlinaFilterChip
import com.sancarlina.app.ui.features.turismo.components.*
import com.sancarlina.app.ui.theme.SancarlinaBackground
import com.sancarlina.app.ui.theme.SancarlinaPrimary
import com.sancarlina.app.ui.theme.SancarlinaSurfaceContainerLow
import com.sancarlina.app.viewmodel.TurismoViewModel

@Composable
fun TurismoContent(
    viewModel: TurismoViewModel = viewModel(),
    onOpenDrawer: () -> Unit = {},
    onNavigateToPoint: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val categoryChips = remember(uiState.points) {
        if (uiState.points.isEmpty()) {
            emptyList()
        } else {
            listOf("Todos") + uiState.points.map { it.category }.distinct().filter { it.isNotBlank() }
        }
    }

    val displayedPoints = remember(uiState.points, uiState.selectedCategory) {
        if (uiState.selectedCategory == "Todos") {
            uiState.points
        } else {
            uiState.points.filter { it.category == uiState.selectedCategory }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
    ) {
        TurismoHeroSection(
            banner = uiState.banners.firstOrNull(),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        TurismoSearchBar(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 4.dp)
        )

        if (categoryChips.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoryChips) { category ->
                    val icon = when (category.lowercase(java.util.Locale.ROOT)) {
                        "todos" -> Icons.Default.Explore
                        "bodegas", "bodegas y vinos", "vino", "vinos" -> Icons.Default.WineBar
                        "gastronomía", "gastronomia", "comida", "restaurante", "restaurantes" -> Icons.Default.Restaurant
                        "historia", "cultura", "museo", "museos" -> Icons.Default.AccountBalance
                        "naturaleza", "paisaje", "paisajes", "parque", "parques", "aventura" -> Icons.Default.Landscape
                        else -> null
                    }

                    SancarlinaFilterChip(
                        label = category,
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) },
                        leadingIcon = if (icon != null) {
                            {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null
                    )
                }
            }
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            }
            uiState.points.isEmpty() -> {
                TurismoEmptyState()
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(displayedPoints.chunked(2)) { rowPoints ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowPoints.forEach { point ->
                                TurismoPointCard(
                                    point = point,
                                    onClick = { onNavigateToPoint(point.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowPoints.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
