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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sancarlina.ui.theme.SancarlinaAccent
import com.example.sancarlina.ui.theme.SancarlinaPrimary
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
fun MapContent(viewModel: MapViewModel = viewModel()) {
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
                // Request current high accuracy location
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()
                
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                }
            } catch (e: Exception) {
                // In case of error or null location, fallback to default or log
            }
        }
    }

    // Auto-track location when permission is granted
    LaunchedEffect(uiState.isLocationPermissionGranted) {
        if (uiState.isLocationPermissionGranted) {
            updateToCurrentLocation()
        }
    }

    val sheetState = rememberModalBottomSheetState()

    Box(modifier = Modifier.fillMaxSize()) {
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
            )
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

        // Category Filters
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
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
                    .padding(bottom = 100.dp, end = 16.dp),
                containerColor = Color.White,
                contentColor = SancarlinaPrimary,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación")
            }
        }

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
                        .padding(bottom = 80.dp) // Space for bottom nav
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Square Image (Vista 18)
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
                                // Star Rating (Vista 18)
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
                        // Button 1: WhatsApp (Guinda/Vino - Vista 18)
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
                        
                        // Button 2: Directions (Primary border - Vista 18)
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
