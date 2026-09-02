package com.sancarlina.app.ui.features.map

import androidx.compose.material3.MaterialTheme

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import android.content.Intent
import androidx.core.net.toUri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaFilterChip
import com.sancarlina.app.ui.features.home.FlowRow
import com.sancarlina.app.ui.features.map.components.*
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.utils.captureBestLocation
import com.sancarlina.app.viewmodel.MapViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapContent(
    viewModel: MapViewModel = viewModel(),
    onOpenDrawer: () -> Unit = {},
    onNavigateToCommerce: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.builder()
            .target(LatLng(-33.7483, -69.0436))
            .zoom(14f)
            .tilt(45f)
            .build()
    }
    val mapStyleOptions = remember(context) {
        runCatching { MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style) }
            .getOrNull()
    }

    var hasFinePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasCoarsePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var isLocating by remember { mutableStateOf(false) }
    var bestAccuracy by remember { mutableStateOf<Float?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var cancelLocationCapture by remember { mutableStateOf<(() -> Unit)?>(null) }
    val hasLocationPermission = hasFinePermission || hasCoarsePermission

    fun animateToLocation(location: Location) {
        scope.launch {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.builder()
                        .target(LatLng(location.latitude, location.longitude))
                        .zoom(if (hasFinePermission) 17f else 15f)
                        .tilt(45f)
                        .build()
                ),
                1000
            )
        }
    }

    fun startLocationCapture(permissionAvailable: Boolean = hasLocationPermission) {
        if (!permissionAvailable || isLocating) return
        cancelLocationCapture?.invoke()
        isLocating = true
        bestAccuracy = null
        locationError = null
        cancelLocationCapture = captureBestLocation(
            client = fusedLocationClient,
            onProgress = { reading ->
                bestAccuracy = reading.accuracy.takeIf {
                    reading.hasAccuracy() && it.isFinite() && it >= 0f
                }
            },
            onResult = { location ->
                isLocating = false
                cancelLocationCapture = null
                animateToLocation(location)
            },
            onError = { message ->
                isLocating = false
                cancelLocationCapture = null
                locationError = message
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        )
    }

    LaunchedEffect(hasLocationPermission) {
        viewModel.onPermissionResult(hasLocationPermission)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasFinePermission = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        hasCoarsePermission = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val granted = hasFinePermission || hasCoarsePermission
        viewModel.onPermissionResult(granted)
        if (granted) {
            startLocationCapture(permissionAvailable = true)
        } else {
            locationError = "Permiso de ubicación denegado. Podés seguir usando el mapa sin tu posición."
        }
    }

    DisposableEffect(Unit) {
        onDispose { cancelLocationCapture?.invoke() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapStyleOptions = mapStyleOptions,
                isBuildingEnabled = true,
                isIndoorEnabled = true
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = false,
                mapToolbarEnabled = false,
                tiltGesturesEnabled = true,
                rotationGesturesEnabled = true,
                zoomGesturesEnabled = true,
                scrollGesturesEnabled = true
            )
        ) {
            uiState.filteredMarkers.forEach { marker ->
                val markerState = rememberUpdatedMarkerState(position = marker.position)
                
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
                        color = MaterialTheme.colorScheme.surface,
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
                                color = MaterialTheme.colorScheme.onSurface
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
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
        ) {
            MapFloatingTopBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChanged,
                onOpenDrawer = onOpenDrawer,
                onOpenFilters = { viewModel.toggleFilterPanel(true) },
                activeFilterCount = listOf(
                    uiState.selectedCategory != "Todos",
                    uiState.selectedLocation != "Todas",
                    uiState.onlyWithSello
                ).count { it }
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
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = if (uiState.filteredMarkers.isNotEmpty()) 178.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shadowElevation = 7.dp
            ) {
                Column {
                    IconButton(onClick = {
                        scope.launch { cameraPositionState.animate(CameraUpdateFactory.zoomIn(), 260) }
                    }) {
                        Icon(Icons.Default.Add, "Acercar mapa", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    HorizontalDivider(
                        modifier = Modifier.width(32.dp).align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    IconButton(onClick = {
                        scope.launch { cameraPositionState.animate(CameraUpdateFactory.zoomOut(), 260) }
                    }) {
                        Icon(Icons.Default.Remove, "Alejar mapa", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
            FloatingActionButton(
                onClick = {
                    if (hasLocationPermission) {
                        startLocationCapture()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                if (isLocating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Mi ubicación",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        if (uiState.filteredMarkers.isNotEmpty() && !uiState.isBottomSheetVisible) {
            MapExplorerDock(
                markers = uiState.filteredMarkers,
                selectedMarkerId = uiState.selectedMarker?.id,
                onSelect = { marker ->
                    viewModel.onMarkerClick(marker)
                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(marker.position, 16.5f),
                            650
                        )
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (isLocating || locationError != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 88.dp, bottom = 20.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = if (isLocating) {
                        bestAccuracy?.let { "Buscando mejor señal · ±${it.roundToInt()} m" }
                            ?: "Buscando tu ubicación…"
                    } else {
                        locationError.orEmpty()
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (locationError != null) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }

        if (uiState.markers.isEmpty()) {
            MapEmptyHint(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 88.dp, bottom = 190.dp)
            )
        }

        if (uiState.isBottomSheetVisible && uiState.selectedMarker != null) {
            MapTenantBottomSheetCard(
                marker = uiState.selectedMarker!!,
                onDismiss = { viewModel.onDismissBottomSheet() },
                onNavigate = { onNavigateToCommerce(uiState.selectedMarker!!.id) },
                onDirections = {
                    val marker = uiState.selectedMarker!!
                    val uri = "google.navigation:q=${marker.position.latitude},${marker.position.longitude}".toUri()
                    val googleMapsIntent = Intent(Intent.ACTION_VIEW, uri).setPackage("com.google.android.apps.maps")
                    val fallbackIntent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.google.com/maps/dir/?api=1&destination=${marker.position.latitude},${marker.position.longitude}".toUri()
                    )
                    runCatching { context.startActivity(googleMapsIntent) }
                        .onFailure { context.startActivity(fallbackIntent) }
                }
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
                Text(stringResource(R.string.map_filter_close), color = MaterialTheme.colorScheme.primary)
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
        containerColor = MaterialTheme.colorScheme.surface,
        shape = SancarlinaCardShape
    )
}
