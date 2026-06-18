package com.sancarlina.app.ui.features.category.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sancarlina.app.ui.components.SancarlinaFilterChip

@Composable
fun CategoryFilterBar(
    locations: List<String>,
    selectedLocation: String,
    onLocationSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (locations.isEmpty()) return

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(locations) { location ->
            SancarlinaFilterChip(
                label = location,
                selected = selectedLocation == location,
                onClick = { onLocationSelected(location) }
            )
        }
    }
}
