package com.sancarlina.app.ui.features.map

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-33.7483, -69.0436), 12f)
    }

    // Lógica para comprobar y solicitar permisos de ubicación del usuario
    val hasLocationPermission = remember(context) {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(hasLocationPermission) {
        viewModel.onPermissionResult(hasLocationPermission)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(isGranted)
        if (isGranted) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(-33.7483, -69.0436), 13f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = uiState.isLocationPermissionGranted),
            uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
        ) {
            uiState.filteredMarkers.forEach { marker ->
                val markerState = rememberMarkerState(position = marker.position)
                
                // Mostrar InfoWindowContent personalizada en forma de popover flotante
                MarkerInfoWindowContent(
                    state = markerState,
                    onClick = {
                        viewModel.onMarkerClick(marker)
                        false // Permite que se despliegue automáticamente la InfoWindow flotante
                    },
                    onInfoWindowClick = {
                        onNavigateToCommerce(marker.id)
                    }
                ) {
                    // Selected Card Info Popover (Estilo Stitch)
                    Surface(
                        color = SancarlinaSurface,
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 3.dp,
                        shadowElevation = 8.dp,
                        modifier = Modifier.width(180.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = marker.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SancarlinaOnSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${marker.rating} (GondolApp)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SancarlinaOnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Buscador superior flotante y chips de categorías
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            MapFloatingTopBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChanged,
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

        // FAB circular de Mi Ubicación (Estilo Stitch)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 96.dp, end = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = {
                    if (uiState.isLocationPermissionGranted) {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(-33.7483, -69.0436), 14f)
                    } else {
                        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                containerColor = SancarlinaPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Mi Ubicación",
                    modifier = Modifier.size(24.dp)
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
