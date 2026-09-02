package com.sancarlina.app.ui.features.favorites

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.features.category.components.CommerceCard
import com.sancarlina.app.ui.features.favorites.components.FavoritesEmptyState
import com.sancarlina.app.viewmodel.CommerceMarker
import com.sancarlina.app.viewmodel.FavoritesViewModel
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun FavoritesContent(
    viewModel: FavoritesViewModel = viewModel(),
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SancarlinaTopBar(
            title = stringResource(R.string.favorites_title),
            onBack = onBack
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            uiState.favorites.isEmpty() -> {
                FavoritesEmptyState(onExplore = onBack)
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.favorites, key = { it.id }) { commerce ->
                        CommerceCard(commerce = commerce) {
                            onNavigateToDetail(commerce.id)
                        }
                    }
                    item { Spacer(modifier = Modifier.height(88.dp)) }
                }
            }
        }
    }
}
