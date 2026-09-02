package com.sancarlina.app.ui.features.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sancarlina.app.R
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.models.displayImageUrl
import com.sancarlina.app.ui.components.SancarlinaCard
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.features.map.components.CommerceInfoCard
import com.sancarlina.app.ui.features.map.components.CommerceActionPanel
import com.sancarlina.app.ui.features.map.components.CommerceProfileHero
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.CommerceProfileViewModel
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

@Composable
fun CommerceProfileContent(
    commerceId: String,
    viewModel: CommerceProfileViewModel,
    onBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToForm: (String) -> Unit = {},
    onNavigateToReviews: (String) -> Unit = {},
    onNavigateToRate: (String) -> Unit = {},
    isInRoute: Boolean = false,
    onToggleRoute: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var distanceKm by remember(commerceId) { mutableStateOf<Float?>(null) }
    LaunchedEffect(commerceId) {
        viewModel.loadCommerce(commerceId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            uiState.error != null || uiState.tenant == null -> {
                CommerceProfileErrorState(
                    message = uiState.error ?: stringResource(R.string.commerce_load_error),
                    onBack = onBack
                )
            }
            else -> {
                val tenant = uiState.tenant!!
                LaunchedEffect(tenant.id, tenant.latitude, tenant.longitude) {
                    val hasPermission =
                        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    val lat = tenant.latitude
                    val lng = tenant.longitude
                    if (hasPermission && lat != null && lng != null) {
                        LocationServices.getFusedLocationProviderClient(context).lastLocation
                            .addOnSuccessListener { location ->
                                if (location != null) {
                                    val result = FloatArray(1)
                                    Location.distanceBetween(location.latitude, location.longitude, lat, lng, result)
                                    distanceKm = result[0] / 1000f
                                }
                            }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    CommerceProfileHero(imageUrl = tenant.displayImageUrl())

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-24).dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        CommerceInfoCard(
                            tenant = tenant,
                            distanceKm = distanceKm,
                            onRatingClick = { onNavigateToReviews(tenant.id) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CommerceActionPanel(
                            tenant = tenant,
                            isInRoute = isInRoute,
                            onTrack = viewModel::trackAction,
                            onToggleRoute = {
                                viewModel.trackAction("add_to_route")
                                onToggleRoute(tenant.id)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        SancarlinaPrimaryButton(
                            text = "Calificar comercio",
                            onClick = { onNavigateToRate(tenant.id) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CommerceProductsSection(onNavigateToProduct = onNavigateToProduct)

                        if (uiState.forms.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.commerce_forms_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            uiState.forms.forEach { form ->
                                FormItem(form = form) {
                                    onNavigateToForm(form.id)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(88.dp))
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.25f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    stringResource(R.string.cd_back),
                    tint = Color.White
                )
            }

            IconButton(
                onClick = { viewModel.toggleFavorite() },
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.25f), CircleShape)
            ) {
                Icon(
                    imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.cd_favorite),
                    tint = if (uiState.isFavorite) Color.Red else Color.White
                )
            }
        }
    }
}

@Composable
private fun CommerceProductsSection(@Suppress("UNUSED_PARAMETER") onNavigateToProduct: (String) -> Unit) {
    SancarlinaCard {
        Text(
            text = stringResource(R.string.commerce_products_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Inventory2,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.commerce_products_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CommerceProfileErrorState(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SancarlinaCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Store,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun FormItem(form: FormSchema, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Assignment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = form.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
