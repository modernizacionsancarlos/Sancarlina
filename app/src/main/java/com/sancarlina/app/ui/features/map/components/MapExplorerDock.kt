package com.sancarlina.app.ui.features.map.components

import androidx.compose.material3.MaterialTheme

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.CommerceMarker

@Composable
fun MapExplorerDock(
    markers: List<CommerceMarker>,
    selectedMarkerId: String?,
    onSelect: (CommerceMarker) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.98f),
        shadowElevation = 12.dp,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(top = 10.dp, bottom = 12.dp)) {
            Surface(
                modifier = Modifier.align(Alignment.CenterHorizontally).width(36.dp).height(4.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = CircleShape
            ) {}
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Explorá cerca tuyo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    AnimatedContent(
                        targetState = markers.size,
                        transitionSpec = { fadeIn(tween(2)) togetherWith fadeOut(tween(2)) using SizeTransform(clip = false) },
                        label = "mapResultCount"
                    ) { count ->
                        Text(
                            text = if (count == 1) "1 lugar encontrado" else "$count lugares encontrados",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp).size(18.dp)
                    )
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(markers.take(12), key = { it.id }) { marker ->
                    val selected = marker.id == selectedMarkerId
                    Surface(
                        modifier = Modifier.width(248.dp).height(82.dp).clickable { onSelect(marker) },
                        shape = RoundedCornerShape(17.dp),
                        color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f) else MaterialTheme.colorScheme.surfaceContainerLowest,
                        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                        shadowElevation = if (selected) 4.dp else 1.dp
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (marker.imageUrl.isNotBlank()) {
                                AsyncImage(
                                    model = marker.imageUrl,
                                    contentDescription = marker.name,
                                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Surface(modifier = Modifier.size(64.dp), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                    Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(marker.category.ifBlank { "Lugar" }, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, maxLines = 1)
                                Text(marker.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null, tint = Color(0xFFF2B01E), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(String.format(java.util.Locale.US, "%.1f", marker.rating), style = MaterialTheme.typography.labelSmall)
                                    Text(" · ${marker.locationName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Ver en el mapa", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
