package com.sancarlina.app.ui.features.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.features.category.components.CommerceCard
import com.sancarlina.app.ui.features.favorites.components.FavoritesEmptyState
import com.sancarlina.app.ui.theme.SancarlinaBackground
import com.sancarlina.app.viewmodel.CommerceMarker

@Composable
fun FavoritesContent(
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val favorites = remember { emptyList<CommerceMarker>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
    ) {
        SancarlinaTopBar(
            title = stringResource(R.string.favorites_title),
            onBack = onBack
        )

        if (favorites.isEmpty()) {
            FavoritesEmptyState(onExplore = onBack)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites, key = { it.id }) { commerce ->
                    CommerceCard(commerce = commerce) {
                        onNavigateToDetail(commerce.id)
                    }
                }
                item { Spacer(modifier = Modifier.height(88.dp)) }
            }
        }
    }
}
