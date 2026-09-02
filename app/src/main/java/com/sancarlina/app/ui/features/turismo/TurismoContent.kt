package com.sancarlina.app.ui.features.turismo

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sancarlina.app.ui.components.SancarlinaFilterChip
import com.sancarlina.app.ui.features.turismo.components.*
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.TurismoViewModel
import com.sancarlina.app.viewmodel.ItineraryViewModel

@Composable
fun TurismoContent(
    viewModel: TurismoViewModel = viewModel(),
    itineraryViewModel: ItineraryViewModel,
    onOpenDrawer: () -> Unit = {},
    onNavigateToItinerary: () -> Unit = {},
    onNavigateToPoint: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val itineraryState by itineraryViewModel.uiState.collectAsState()

    val categoryChips = remember(uiState.points) {
        if (uiState.points.isEmpty()) {
            emptyList()
        } else {
            listOf("Todos") + uiState.points.map { it.category }.distinct().filter { it.isNotBlank() }
        }
    }

    val displayedPoints = remember(uiState.points, uiState.selectedCategory, uiState.searchQuery) {
        uiState.points.filter { point ->
            val categoryMatches = uiState.selectedCategory == "Todos" || point.category == uiState.selectedCategory
            val queryMatches = uiState.searchQuery.isBlank() ||
                point.name.contains(uiState.searchQuery, ignoreCase = true) ||
                point.description.contains(uiState.searchQuery, ignoreCase = true) ||
                point.location.contains(uiState.searchQuery, ignoreCase = true)
            categoryMatches && queryMatches
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TurismoHeroSection(
            banner = uiState.banners.firstOrNull(),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        TurismoSearchBar(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchQueryChanged,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 4.dp)
        )

        Surface(
            onClick = onNavigateToItinerary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Route, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(27.dp))
                Spacer(Modifier.width(11.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Armá tu día", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        if (itineraryState.points.isEmpty()) "Elegí lugares y creá tu recorrido" else "${itineraryState.points.size} paradas seleccionadas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Ver recorrido", tint = MaterialTheme.colorScheme.primary)
            }
        }

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
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
                    item {
                        androidx.compose.material3.Text(
                            text = if (displayedPoints.size == 1) "1 experiencia para descubrir" else "${displayedPoints.size} experiencias para descubrir",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(displayedPoints, key = { it.id }) { point ->
                        TurismoPointCard(
                            point = point,
                            onClick = { onNavigateToPoint(point.id) },
                            isInRoute = point.id in itineraryState.selectedIds,
                            onToggleRoute = { itineraryViewModel.toggle(point.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
