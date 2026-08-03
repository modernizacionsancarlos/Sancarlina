package com.sancarlina.app.ui.features.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.CategoryItem

@Composable
fun HomeCategoryGrid(
    categories: List<CategoryItem>,
    onCategoryClick: (CategoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for (i in categories.indices step 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeCategoryTile(
                    category = categories[i],
                    modifier = Modifier.weight(1f),
                    onClick = { onCategoryClick(categories[i]) }
                )
                if (i + 1 < categories.size) {
                    HomeCategoryTile(
                        category = categories[i + 1],
                        modifier = Modifier.weight(1f),
                        onClick = { onCategoryClick(categories[i + 1]) }
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                if (i + 2 < categories.size) {
                    HomeCategoryTile(
                        category = categories[i + 2],
                        modifier = Modifier.weight(1f),
                        onClick = { onCategoryClick(categories[i + 2]) }
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HomeCategoryTile(
    category: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .aspectRatio(0.92f)
            .clickable(onClick = onClick),
        shape = SancarlinaCardShape,
        color = SancarlinaSurfaceContainerLow,
        border = BorderStroke(1.dp, SancarlinaOutlineVariant.copy(alpha = 0.3f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                color = SancarlinaPrimaryContainer.copy(alpha = 0.2f),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (category.iconUrl.isNotBlank()) {
                        AsyncImage(
                            model = category.iconUrl,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            imageVector = categoryIconFor(category.name),
                            contentDescription = null,
                            tint = SancarlinaPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name.formatCategoryLabel(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = SancarlinaOnSurface,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

private fun categoryIconFor(name: String) = when {
    name.contains("BODEGA", ignoreCase = true) -> Icons.Default.WineBar
    name.contains("GASTRONOM", ignoreCase = true) -> Icons.Default.Restaurant
    name.contains("ARTESAN", ignoreCase = true) -> Icons.Default.Palette
    name.contains("ALOJAMIENTO", ignoreCase = true) -> Icons.Default.Bed
    name.contains("SERVICIO", ignoreCase = true) -> Icons.Default.Store
    else -> Icons.Default.Category
}

private fun String.formatCategoryLabel(): String =
    replace("\n", " ")
        .split(" ")
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { c -> c.uppercase() }
        }
