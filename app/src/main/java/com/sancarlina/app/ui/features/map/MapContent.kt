package com.sancarlina.app.ui.features.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaFilterChip
import com.sancarlina.app.ui.features.home.FlowRow
import com.sancarlina.app.ui.features.map.components.*
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapContent(
    viewModel: MapViewModel = viewModel(),
    onOpenDrawer: () -> Unit = {},
    onNavigateToCommerce: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-33.7483, -69.0436), 12f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
        ) {
            uiState.filteredMarkers.forEach { marker ->
                Marker(
                    state = MarkerState(position = marker.position),
                    title = marker.name,
                    onClick = {
                        viewModel.onMarkerClick(marker)
                        true
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            MapFloatingTopBar(
                onOpenDrawer = onOpenDrawer,
                onOpenFilters = { viewModel.toggleFilterPanel(true) }
            )

            if (uiState.markers.isNotEmpty() && uiState.categories.size > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                MapFilterChips(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = viewModel::onCategorySelected
                )
            }
        }

        if (uiState.markers.isEmpty()) {
            MapEmptyHint(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
            )
        }

        if (uiState.isBottomSheetVisible && uiState.selectedMarker != null) {
            MapTenantBottomSheetCard(
                marker = uiState.selectedMarker!!,
                onDismiss = { viewModel.onDismissBottomSheet() },
                onNavigate = { onNavigateToCommerce(uiState.selectedMarker!!.id) }
            )
        }

        if (uiState.isFilterPanelVisible) {
            MapFilterDialog(
                onDismiss = { viewModel.toggleFilterPanel(false) },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun MapFilterDialog(onDismiss: () -> Unit, viewModel: MapViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.map_filter_close), color = SancarlinaPrimary)
            }
        },
        title = {
            Text(
                stringResource(R.string.map_filter_title),
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    stringResource(R.string.map_filter_categories),
                    style = MaterialTheme.typography.labelLarge
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.categories.forEach { category ->
                        SancarlinaFilterChip(
                            label = category,
                            selected = uiState.selectedCategory == category,
                            onClick = { viewModel.onCategorySelected(category) }
                        )
                    }
                }

                Text(
                    stringResource(R.string.map_filter_locations),
                    style = MaterialTheme.typography.labelLarge
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.locations.forEach { location ->
                        SancarlinaFilterChip(
                            label = location,
                            selected = uiState.selectedLocation == location,
                            onClick = { viewModel.onLocationSelected(location) }
                        )
                    }
                }
            }
        },
        containerColor = SancarlinaSurface,
        shape = SancarlinaCardShape
    )
}
