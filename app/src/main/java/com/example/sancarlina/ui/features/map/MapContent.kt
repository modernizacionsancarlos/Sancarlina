package com.example.sancarlina.ui.features.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sancarlina.ui.theme.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Estilo de mapa sutil (Vista 18)
const val MAP_STYLE_JSON = """
[
  {
    "featureType": "all",
    "elementType": "geometry.fill",
    "stylers": [ { "color": "#f9fbea" } ]
  },
  {
    "featureType": "road",
    "elementType": "geometry",
    "stylers": [ { "color": "#edefdf" } ]
  },
  {
    "featureType": "water",
    "elementType": "geometry",
    "stylers": [ { "color": "#e2e4d3" } ]
  },
  {
    "featureType": "landscape.natural",
    "elementType": "geometry",
    "stylers": [ { "color": "#f0f2e1" } ]
  }
]
"""

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapContent(
    viewModel: MapViewModel = viewModel(),
    onOpenDrawer: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()
    
    // Valle de Uco default position
    val valleDeUco = LatLng(-33.7483, -69.0436)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(valleDeUco, 11f)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(isGranted)
    }

    LaunchedEffect(Unit) {
        val permissionCheckResult = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            viewModel.onPermissionResult(true)
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // High precision location tracking
    fun updateToCurrentLocation() {
        scope.launch {
            try {
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()
                
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                }
            } catch (e: Exception) {
            }
        }
    }

    LaunchedEffect(uiState.filteredMarkers) {
        if (uiState.filteredMarkers.isNotEmpty()) {
            val builder = com.google.android.gms.maps.model.LatLngBounds.Builder()
            uiState.filteredMarkers.forEach { builder.include(it.position) }
            val bounds = builder.build()
            
            if (uiState.filteredMarkers.size == 1) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(uiState.filteredMarkers[0].position, 15f)
                )
            } else {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds, 150)
                )
            }
        }
    }

    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "GÓNDOLA SANCARLINA",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFilterPanel(true) }) {
                        Icon(Icons.Default.Tune, contentDescription = "Filtros", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SancarlinaPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = uiState.isLocationPermissionGranted,
                    mapType = MapType.NORMAL,
                    mapStyleOptions = MapStyleOptions(MAP_STYLE_JSON)
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                ),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                uiState.filteredMarkers.forEach { commerce ->
                    Marker(
                        state = MarkerState(position = commerce.position),
                        title = commerce.name,
                        snippet = commerce.locationName,
                        onClick = {
                            viewModel.onMarkerClick(commerce)
                            true
                        }
                    )
                }
            }

            // Category Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.categories) { category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category,
                            onClick = { viewModel.onCategorySelected(category) },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SancarlinaPrimary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // My Location Button
            if (uiState.isLocationPermissionGranted) {
                FloatingActionButton(
                    onClick = { updateToCurrentLocation() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 24.dp, end = 16.dp),
                    containerColor = Color.White,
                    contentColor = SancarlinaPrimary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación")
                }
            }

            // Advanced Filter Panel [VISTA 19]
            if (uiState.isFilterPanelVisible) {
                AdvancedFilterPanel(
                    uiState = uiState,
                    onDismiss = { viewModel.toggleFilterPanel(false) },
                    onLocationSelected = { viewModel.onLocationSelected(it) },
                    onSelloToggled = { viewModel.onSelloToggled(it) },
                    onClearFilters = { viewModel.clearFilters() },
                    onApplyFilters = { viewModel.toggleFilterPanel(false) }
                )
            }

            // Detail Bottom Sheet [VISTA 18]
            if (uiState.isBottomSheetVisible && uiState.selectedMarker != null) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.onDismissBottomSheet() },
                    sheetState = sheetState,
                    containerColor = Color.White,
                    dragHandle = {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp, bottom = 20.dp)
                                .width(48.dp)
                                .height(6.dp)
                                .background(Color(0xFFE2E4D3), RoundedCornerShape(3.dp))
                        )
                    }
                ) {
                    val marker = uiState.selectedMarker!!
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 32.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = marker.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = marker.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = marker.category,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    repeat(5) { index ->
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (index < marker.rating.toInt()) Color(0xFFF59E0B) else Color.LightGray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = marker.distance,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = Uri.parse("https://wa.me/${marker.phone}")
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("CONTACTO WHATSAPP", style = MaterialTheme.typography.labelLarge)
                            }
                            
                            OutlinedButton(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("geo:${marker.position.latitude},${marker.position.longitude}?q=${Uri.encode(marker.name)}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                },
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(0.dp),
                                border = BorderStroke(2.dp, SancarlinaPrimary)
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null, tint = SancarlinaPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedFilterPanel(
    uiState: MapUiState,
    onDismiss: () -> Unit,
    onLocationSelected: (String) -> Unit,
    onSelloToggled: (Boolean) -> Unit,
    onClearFilters: () -> Unit,
    onApplyFilters: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Filtros Avanzados",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Section 1: Localidad
            Text(
                text = "POR LOCALIDAD",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                uiState.locations.forEach { location ->
                    FilterChip(
                        selected = uiState.selectedLocation == location,
                        onClick = { onLocationSelected(location) },
                        label = { Text(location) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SancarlinaAccent,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Section 2: Sello de Origen
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF3F5E4),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E4D3))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Verified, 
                        contentDescription = null, 
                        tint = SancarlinaPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sello de Origen",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Mostrar solo certificados",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = uiState.onlyWithSello,
                        onCheckedChange = onSelloToggled,
                        colors = SwitchDefaults.colors(checkedThumbColor = SancarlinaPrimary)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextButton(
                    onClick = onClearFilters,
                    modifier = Modifier.weight(0.4f)
                ) {
                    Text("LIMPIAR", color = SancarlinaAccent, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onApplyFilters,
                    modifier = Modifier.weight(0.6f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("APLICAR FILTROS", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        content = { content() }
    )
}
