package com.sancarlina.app.ui.features.turismo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SancarlinaSurfaceContainerLow)
        ) {
            TurismoHeroSection()
            TurismoSearchBar(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (categoryChips.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoryChips.forEach { category ->
                    SancarlinaFilterChip(
                        label = category,
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) }
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
                    items(displayedPoints, key = { it.id }) { point ->
                        TurismoPointCard(
                            point = point,
                            onClick = { onNavigateToPoint(point.id) }
                        )
                    }
                }
            }
        }
    }
}
