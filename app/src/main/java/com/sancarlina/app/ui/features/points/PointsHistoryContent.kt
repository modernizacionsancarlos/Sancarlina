package com.sancarlina.app.ui.features.points

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sancarlina.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsHistoryContent(onBack: () -> Unit) {
    val movements = listOf(
        PointMovement("1", "Compra en Almacén Regional", "20 Mayo", 150, true),
        PointMovement("2", "Descuento en Bodega La Celia", "15 Mayo", -500, false),
        PointMovement("3", "Compra en Artesanías del Valle", "10 Mayo", 200, true),
        PointMovement("4", "Cafetería Plaza Central", "5 Mayo", 50, true)
    )

    Box(modifier = Modifier.fillMaxSize().background(SancarlinaSurface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SancarlinaSurfaceContainerLow
            ) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SancarlinaPrimary)
                    }
                    Text(
                        text = "Historial de Puntos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Actividad Reciente",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface,
                        modifier = Modifier.padding(bottom = 8.dp, top = 12.dp)
                    )
                }
                
                items(movements) { movement ->
                    MovementCard(movement)
                }
                
                item { Spacer(Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun MovementCard(movement: PointMovement) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SancarlinaSurfaceContainerLowest,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (movement.isEarned) SancarlinaPrimary.copy(alpha = 0.1f) else SancarlinaSecondary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (movement.isEarned) Icons.Default.AddCircleOutline else Icons.Default.RemoveCircleOutline,
                        contentDescription = null,
                        tint = if (movement.isEarned) SancarlinaPrimary else SancarlinaSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movement.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = SancarlinaOnSurface
                )
                Text(
                    text = movement.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = SancarlinaOutline
                )
            }
            
            Text(
                text = "${if (movement.amount > 0) "+" else ""}${movement.amount}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (movement.isEarned) SancarlinaPrimary else SancarlinaSecondary
            )
        }
    }
}

data class PointMovement(
    val id: String,
    val title: String,
    val date: String,
    val amount: Int,
    val isEarned: Boolean
)
