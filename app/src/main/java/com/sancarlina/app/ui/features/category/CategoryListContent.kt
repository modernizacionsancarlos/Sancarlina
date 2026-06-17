package com.sancarlina.app.ui.features.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.sancarlina.app.viewmodel.CommerceMarker
import com.sancarlina.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListContent(
    categoryId: String,
    viewModel: CategoryListViewModel = viewModel(),
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

    LaunchedEffect(categoryId) {
        viewModel.loadCategory(categoryId)
    }

    Box(modifier = Modifier.fillMaxSize().background(SancarlinaSurface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // App Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SancarlinaSurfaceContainerLow
            ) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_back), tint = SancarlinaPrimary)
                    }
                    Text(
                        text = uiState.categoryName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showFilters = true }) {
                        Icon(Icons.Default.Tune, stringResource(R.string.cd_filters), tint = SancarlinaPrimary)
                    }
                }
            }

            // Quick Location Chips
            LazyRow(
                modifier = Modifier.padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.locations) { location ->
                    FilterChip(
                        selected = uiState.selectedLocation == location,
                        onClick = { viewModel.onLocationSelected(location) },
                        label = { Text(location) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SancarlinaPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = SancarlinaSurfaceContainer,
                            labelColor = SancarlinaOnSurfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = null
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            } else if (uiState.filteredCommerces.isEmpty()) {
                CategoryListEmptyState(hasLoadError = uiState.hasLoadError)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.filteredCommerces) { commerce ->
                        CommerceCard(commerce) {
                            onNavigateToDetail(commerce.id)
                        }
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }

        if (showFilters) {
            AdvancedFiltersBottomSheet(
                onDismiss = { showFilters = false },
                onApply = { /* Apply logic */ }
            )
        }
    }
}

@Composable
fun CategoryListEmptyState(hasLoadError: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Storefront,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = SancarlinaSecondary.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.category_list_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = SancarlinaOnSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            text = stringResource(
                if (hasLoadError) R.string.category_list_error_message
                else R.string.category_list_empty_message
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = SancarlinaOnSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
fun CommerceCard(commerce: CommerceMarker, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = SancarlinaSurfaceContainerLowest,
        shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = commerce.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 4.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = commerce.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SancarlinaOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = SancarlinaOutline, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = commerce.locationName,
                        style = MaterialTheme.typography.labelMedium,
                        color = SancarlinaOnSurfaceVariant,
                        maxLines = 1
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = commerce.rating.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    if (commerce.distance.isNotEmpty()) {
                        Text(
                            text = commerce.distance,
                            style = MaterialTheme.typography.labelSmall,
                            color = SancarlinaOutline
                        )
                    }
                }
            }
            
            IconButton(onClick = { /* Favorite */ }) {
                Icon(Icons.Default.FavoriteBorder, stringResource(R.string.cd_favorite), tint = SancarlinaSecondary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedFiltersBottomSheet(onDismiss: () -> Unit, onApply: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SancarlinaSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = SancarlinaOutlineVariant) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                "Filtros Avanzados",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = SancarlinaPrimary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Distancia", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            var sliderValue by remember { mutableFloatStateOf(5f) }
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = 0f..20f,
                colors = SliderDefaults.colors(thumbColor = SancarlinaPrimary, activeTrackColor = SancarlinaPrimary)
            )
            Text("${sliderValue.toInt()} km", style = MaterialTheme.typography.bodySmall, color = SancarlinaOutline)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Calificación mínima", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                repeat(5) { index ->
                    val starIndex = index + 1
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = { Text("$starIndex★") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { onApply(); onDismiss() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Aplicar Filtros", fontWeight = FontWeight.Bold)
            }
        }
    }
}
