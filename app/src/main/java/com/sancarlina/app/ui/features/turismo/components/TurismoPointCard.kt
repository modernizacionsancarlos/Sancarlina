package com.sancarlina.app.ui.features.turismo.components

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.TurismoPoint

@Composable
fun TurismoPointCard(
    point: TurismoPoint,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isInRoute: Boolean = false,
    onToggleRoute: () -> Unit = {}
) {
    Card(
        modifier = modifier.height(232.dp).clickable(onClick = onClick),
        shape = SancarlinaCardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (point.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = point.imageUrl,
                    contentDescription = point.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Image, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f), modifier = Modifier.size(56.dp))
                }
            }

            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.04f), Color.Black.copy(alpha = 0.84f)),
                        startY = 48f
                    )
                )
            )

            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.94f),
                shape = CircleShape,
                modifier = Modifier.align(Alignment.TopStart).padding(14.dp)
            ) {
                Text(
                    text = point.category.ifBlank { "Experiencia" },
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            Column(
                modifier = Modifier.align(Alignment.TopEnd).padding(14.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                if (point.rating > 0.0) {
                    Surface(color = Color.White.copy(alpha = 0.94f), shape = CircleShape) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFF2B01E), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(String.format(java.util.Locale.US, "%.1f", point.rating), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Surface(
                    onClick = onToggleRoute,
                    color = if (isInRoute) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.96f),
                    contentColor = if (isInRoute) Color.White else MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (isInRoute) Icons.Default.Check else Icons.Default.Add,
                            if (isInRoute) "Quitar del recorrido" else "Agregar al recorrido",
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.Route, null, modifier = Modifier.size(17.dp))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart).padding(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = point.name,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (point.location.isNotBlank()) {
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = Color.White.copy(alpha = 0.88f), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(point.location, color = Color.White.copy(alpha = 0.88f), style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Surface(shape = CircleShape, color = Color.White, shadowElevation = 3.dp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Ver experiencia", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(12.dp).size(21.dp))
                }
            }
        }
    }
}
