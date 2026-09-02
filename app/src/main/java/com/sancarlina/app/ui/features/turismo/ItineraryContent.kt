package com.sancarlina.app.ui.features.turismo

import androidx.compose.material3.MaterialTheme

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sancarlina.app.ui.components.SancarlinaCard
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.ItineraryViewModel
import com.sancarlina.app.viewmodel.TurismoPoint
import java.util.Locale

@Composable
fun ItineraryContent(
    viewModel: ItineraryViewModel,
    onBack: () -> Unit,
    onNavigateToPoint: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val mapsUrl = remember(uiState.points) { buildMapsUrl(uiState.points) }

    Column(modifier = Modifier.fillMaxSize()) {
        SancarlinaTopBar(title = "Armá tu día", onBack = onBack)
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.points.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Route, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(72.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Tu recorrido está vacío", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(
                        "Agregá experiencias desde Turismo para organizar tu día.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SancarlinaCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Tu recorrido por San Carlos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                ItineraryMetric(Icons.Default.Place, "${uiState.points.size} paradas")
                                ItineraryMetric(Icons.Default.Straighten, String.format(Locale.US, "%.1f km", uiState.totalDistanceKm))
                                ItineraryMetric(Icons.Default.Schedule, formatMinutes(uiState.estimatedMinutes))
                            }
                            Text(
                                "Tiempo estimado: traslado más 1 hora promedio en cada parada.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                itemsIndexed(uiState.points, key = { _, point -> point.id }) { index, point ->
                    ItineraryStopCard(
                        point = point,
                        number = index + 1,
                        canMoveUp = index > 0,
                        canMoveDown = index < uiState.points.lastIndex,
                        onMoveUp = { viewModel.move(point.id, -1) },
                        onMoveDown = { viewModel.move(point.id, 1) },
                        onRemove = { viewModel.toggle(point.id) },
                        onClick = { onNavigateToPoint(point.id) }
                    )
                }
                item {
                    Button(
                        onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl))) },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        enabled = mapsUrl.isNotBlank(),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Icon(Icons.Default.Navigation, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Abrir recorrido en Google Maps", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            val names = uiState.points.mapIndexed { index, point -> "${index + 1}. ${point.name}" }
                            val text = "Mi recorrido en San Carlos:\n" + names.joinToString("\n") + "\n\n" + mapsUrl
                            context.startActivity(
                                Intent.createChooser(
                                    Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, text)
                                    },
                                    "Compartir recorrido"
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Icon(Icons.Default.Share, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Compartir recorrido")
                    }
                    TextButton(onClick = viewModel::clear, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.DeleteOutline, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Vaciar recorrido")
                    }
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun ItineraryMetric(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ItineraryStopCard(
    point: TurismoPoint,
    number: Int,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(onClick = onClick, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(14.dp)) {
                Box(Modifier.size(34.dp), contentAlignment = Alignment.Center) {
                    Text(number.toString(), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(9.dp))
            AsyncImage(
                model = point.imageUrl,
                contentDescription = point.name,
                modifier = Modifier.size(72.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(point.name, fontWeight = FontWeight.Bold, maxLines = 2)
                Text(point.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            Column {
                IconButton(onClick = onMoveUp, enabled = canMoveUp) { Icon(Icons.Default.KeyboardArrowUp, "Subir parada") }
                IconButton(onClick = onMoveDown, enabled = canMoveDown) { Icon(Icons.Default.KeyboardArrowDown, "Bajar parada") }
                IconButton(onClick = onRemove) { Icon(Icons.Default.Close, "Quitar parada", tint = MaterialTheme.colorScheme.secondary) }
            }
        }
    }
}

private fun buildMapsUrl(points: List<TurismoPoint>): String {
    val located = points.filter { it.latitude != 0.0 && it.longitude != 0.0 }
    if (located.isEmpty()) return ""
    val destination = located.last()
    val builder = Uri.parse("https://www.google.com/maps/dir/").buildUpon()
        .appendQueryParameter("api", "1")
        .appendQueryParameter("destination", "${destination.latitude},${destination.longitude}")
        .appendQueryParameter("travelmode", "driving")
    if (located.size > 1) {
        builder.appendQueryParameter(
            "waypoints",
            located.dropLast(1).joinToString("|") { "${it.latitude},${it.longitude}" }
        )
    }
    return builder.build().toString()
}

private fun formatMinutes(minutes: Int): String {
    val hours = minutes / 60
    val rest = minutes % 60
    return when {
        hours == 0 -> "$rest min"
        rest == 0 -> "$hours h"
        else -> "$hours h $rest min"
    }
}
