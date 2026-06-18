package com.sancarlina.app.ui.features.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaCard
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.features.category.components.CategoryFilterBar
import com.sancarlina.app.ui.features.category.components.CategoryHeader
import com.sancarlina.app.ui.features.category.components.CommerceListCard
import com.sancarlina.app.ui.theme.*

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
    ) {
        CategoryHeader(
            title = uiState.categoryName.ifBlank { categoryId },
            onBack = onBack,
            onOpenFilters = { showFilters = true }
        )

        if (uiState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = SancarlinaPrimary
            )
        }

        CategoryFilterBar(
            locations = uiState.locations,
            selectedLocation = uiState.selectedLocation,
            onLocationSelected = viewModel::onLocationSelected,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        when {
            uiState.isLoading && uiState.filteredCommerces.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            }
            uiState.filteredCommerces.isEmpty() -> {
                CategoryListEmptyState(hasLoadError = uiState.hasLoadError)
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.filteredCommerces, key = { it.id }) { commerce ->
                        CommerceListCard(
                            commerce = commerce,
                            onClick = { onNavigateToDetail(commerce.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(88.dp)) }
                }
            }
        }
    }

    if (showFilters) {
        AdvancedFiltersBottomSheet(
            onDismiss = { showFilters = false },
            onApply = { showFilters = false }
        )
    }
}

@Composable
fun CategoryListEmptyState(hasLoadError: Boolean) {
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
                    Icons.Default.Storefront,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = SancarlinaSecondary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.category_list_empty_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = SancarlinaOnSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(
                        if (hasLoadError) R.string.category_list_error_message
                        else R.string.category_list_empty_message
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SancarlinaOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
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
        shape = SancarlinaSheetShape,
        dragHandle = { BottomSheetDefaults.DragHandle(color = SancarlinaOutlineVariant) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                stringResource(R.string.category_filters_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = SancarlinaPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Distancia",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = SancarlinaOnSurface
            )
            var sliderValue by remember { mutableFloatStateOf(5f) }
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = 0f..20f,
                colors = SliderDefaults.colors(
                    thumbColor = SancarlinaPrimary,
                    activeTrackColor = SancarlinaPrimary
                )
            )
            Text(
                "${sliderValue.toInt()} km",
                style = MaterialTheme.typography.bodySmall,
                color = SancarlinaOutline
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Calificación mínima",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = SancarlinaOnSurface
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
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

            Spacer(modifier = Modifier.height(32.dp))

            SancarlinaPrimaryButton(
                text = stringResource(R.string.category_filters_apply),
                onClick = onApply
            )
        }
    }
}
