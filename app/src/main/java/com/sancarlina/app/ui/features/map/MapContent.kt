package com.sancarlina.app.ui.features.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.MapViewModel
import com.sancarlina.app.viewmodel.CommerceMarker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapContent(
    viewModel: MapViewModel = viewModel(),
    onOpenDrawer: () -> Unit = {},
    onNavigateToCommerce: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(com.google.android.gms.maps.model.LatLng(-33.7483, -69.0436), 12f)
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

        // Top App Bar Overlaid
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding(),
            color = SancarlinaSurfaceContainerLowest.copy(alpha = 0.9f),
            shape = RoundedCornerShape(28.dp),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onOpenDrawer) {
                    Icon(Icons.Default.Menu, stringResource(R.string.cd_menu), tint = SancarlinaPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mapa Sancarlino",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = SancarlinaOnSurface
                )
                IconButton(onClick = { viewModel.toggleFilterPanel(true) }) {
                    Icon(Icons.Default.Tune, stringResource(R.string.cd_filters), tint = SancarlinaPrimary)
                }
            }
        }

        // Bottom Sheet Logic
        if (uiState.isBottomSheetVisible && uiState.selectedMarker != null) {
            CommerceBottomSheet(
                marker = uiState.selectedMarker!!,
                onDismiss = { viewModel.onDismissBottomSheet() },
                onNavigate = { onNavigateToCommerce(uiState.selectedMarker!!.id) }
            )
        }
        
        // Filter Panel
        if (uiState.isFilterPanelVisible) {
            MapFilterDialog(
                onDismiss = { viewModel.toggleFilterPanel(false) },
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommerceBottomSheet(marker: CommerceMarker, onDismiss: () -> Unit, onNavigate: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SancarlinaSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = SancarlinaOutlineVariant) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = marker.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = marker.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface
                    )
                    Text(
                        text = marker.locationName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SancarlinaOnSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = marker.rating.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SancarlinaOnSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNavigate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Ver Perfil Completo", fontWeight = FontWeight.Medium)
            }
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
                Text("Cerrar", color = SancarlinaPrimary)
            }
        },
        title = {
            Text("Filtrar Mapa", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Categorías", style = MaterialTheme.typography.labelLarge)
                com.sancarlina.app.ui.features.home.FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.categories.forEach { category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category,
                            onClick = { viewModel.onCategorySelected(category) },
                            label = { Text(category) },
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
                
                Text("Localidades", style = MaterialTheme.typography.labelLarge)
                com.sancarlina.app.ui.features.home.FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.locations.forEach { location ->
                        FilterChip(
                            selected = uiState.selectedLocation == location,
                            onClick = { viewModel.onLocationSelected(location) },
                            label = { Text(location) },
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }
        },
        containerColor = SancarlinaSurface,
        shape = RoundedCornerShape(28.dp)
    )
}
