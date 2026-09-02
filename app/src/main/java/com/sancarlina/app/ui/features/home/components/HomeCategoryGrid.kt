package com.sancarlina.app.ui.features.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.CategoryItem

@Composable
fun HomeCategoryChips(
    categories: List<CategoryItem>,
    onCategoryClick: (CategoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 1.dp, vertical = 3.dp)
    ) {
        items(categories.take(6), key = { "quick_${it.name}" }) { category ->
            Surface(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clickable { onCategoryClick(category) },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(start = 7.dp, end = 15.dp, top = 6.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = categoryIconFor(category.name),
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(9.dp))
                    Text(
                        text = category.name.formatCategoryLabel(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun HomeCategoryGrid(
    categories: List<CategoryItem>,
    onCategoryClick: (CategoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val columnCount = if (maxWidth >= 700.dp) 3 else 2
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            for (i in categories.indices step columnCount) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(columnCount) { offset ->
                        val category = categories.getOrNull(i + offset)
                        if (category != null) {
                            HomeCategoryTile(
                                category = category,
                                modifier = Modifier.weight(1f),
                                onClick = { onCategoryClick(category) }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
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
    val accent = when {
        category.name.contains("BODEGA", ignoreCase = true) -> Color(0xFF617A28)
        category.name.contains("GASTRONOM", ignoreCase = true) -> Color(0xFF9E334A)
        category.name.contains("ARTESAN", ignoreCase = true) -> Color(0xFFB27A18)
        category.name.contains("ALOJAMIENTO", ignoreCase = true) -> Color(0xFF536B58)
        else -> MaterialTheme.colorScheme.primary
    }
    Surface(
        modifier = modifier
            .aspectRatio(1.08f)
            .clickable(onClick = onClick),
        shape = SancarlinaCardShape,
        color = accent,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f)),
        shadowElevation = 5.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.linearGradient(
                    listOf(accent.copy(alpha = 0.96f), accent.copy(alpha = 0.72f), Color.Black.copy(alpha = 0.30f))
                )
            )
        ) {
            Surface(
                modifier = Modifier.size(88.dp).offset(x = 78.dp, y = (-18).dp),
                color = Color.White.copy(alpha = 0.10f),
                shape = CircleShape
            ) {}
            Surface(
                modifier = Modifier.padding(14.dp).size(46.dp).align(Alignment.TopStart),
                color = Color.White.copy(alpha = 0.94f),
                shape = CircleShape,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (category.iconUrl.isNotBlank()) {
                        AsyncImage(
                            model = category.iconUrl,
                            contentDescription = null,
                            modifier = Modifier.size(27.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            imageVector = categoryIconFor(category.name),
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(27.dp)
                        )
                    }
                }
            }
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(14.dp)) {
                Text(
                    text = category.name.formatCategoryLabel(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Descubrí más", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.88f))
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

internal fun categoryIconFor(name: String) = when {
    name.contains("BODEGA", ignoreCase = true) -> Icons.Default.WineBar
    name.contains("GASTRONOM", ignoreCase = true) -> Icons.Default.Restaurant
    name.contains("ARTESAN", ignoreCase = true) -> Icons.Default.Palette
    name.contains("ALOJAMIENTO", ignoreCase = true) -> Icons.Default.Bed
    name.contains("SERVICIO", ignoreCase = true) -> Icons.Default.Store
    else -> Icons.Default.Category
}

internal fun String.formatCategoryLabel(): String =
    replace("\n", " ")
        .split(" ")
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { c -> c.uppercase() }
        }
