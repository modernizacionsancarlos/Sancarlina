package com.sancarlina.app.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sancarlina.app.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sancarlina.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    viewModel: SearchViewModel = viewModel(),
    onBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit = {},
    onNavigateToCommerce: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val suggestedCategories = listOf("Vinos", "Miel", "Artesanías", "Turismo", "Gastronomía")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaSurface)
    ) {
        // Search Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SancarlinaSurfaceContainerLow
        ) {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_back), tint = SancarlinaPrimary)
                }
                
                TextField(
                    value = uiState.query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    placeholder = { Text("¿Qué estás buscando?", color = SancarlinaOutline) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = SancarlinaOutline) },
                    trailingIcon = {
                        if (uiState.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChange("") }) {
                                Icon(Icons.Default.Close, stringResource(R.string.cd_close), tint = SancarlinaOutline)
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SancarlinaSurfaceContainerLowest,
                        unfocusedContainerColor = SancarlinaSurfaceContainerLowest,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = SancarlinaPrimary
                    ),
                    singleLine = true
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            if (uiState.query.isEmpty()) {
                // Initial State: Recent / Suggestions
                Text(
                    "BÚSQUEDAS SUGERIDAS",
                    style = MaterialTheme.typography.labelLarge,
                    color = SancarlinaOnSurfaceVariant,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    suggestedCategories.forEach { category ->
                        Surface(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .clickable { viewModel.onQueryChange(category) },
                            color = SancarlinaSurfaceContainer,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = category,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = SancarlinaOnSurface
                            )
                        }
                    }
                }
            } else if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            } else if (uiState.results.isEmpty() && uiState.query.length >= 2) {
                // No results
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(64.dp), tint = SancarlinaOutlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No encontramos resultados",
                        style = MaterialTheme.typography.titleMedium,
                        color = SancarlinaOnSurfaceVariant
                    )
                    Text(
                        "Intenta con otras palabras clave",
                        style = MaterialTheme.typography.bodySmall,
                        color = SancarlinaOutline
                    )
                }
            } else {
                // Results List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.results) { result ->
                        SearchResultItem(result) {
                            if (result.type == "PRODUCT") onNavigateToProduct(result.id)
                            else onNavigateToCommerce(result.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(result: SearchResult, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = SancarlinaSurfaceContainerLowest,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = if (result.type == "PRODUCT") Icons.Default.Inventory2 else Icons.Default.Storefront
            Surface(
                color = SancarlinaPrimary.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = SancarlinaPrimary, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = result.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = SancarlinaOnSurface
                )
                Text(
                    text = result.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = SancarlinaOnSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = SancarlinaOutlineVariant)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
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
