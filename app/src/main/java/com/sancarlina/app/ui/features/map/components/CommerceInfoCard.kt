package com.sancarlina.app.ui.features.map.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.R
import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.ui.components.SancarlinaCard
import com.sancarlina.app.ui.components.SancarlinaChip

@Composable
fun CommerceInfoCard(
    tenant: Tenant,
    modifier: Modifier = Modifier,
    distanceKm: Float? = null,
    onRatingClick: () -> Unit = {}
) {
    SancarlinaCard(modifier = modifier) {
        Text(
            text = tenant.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (tenant.industry.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Category,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tenant.industry,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (tenant.rating > 0.0) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onRatingClick() }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                val reviewsSuffix = if (tenant.reviewsCount > 0) {
                    " (${tenant.reviewsCount})"
                } else ""
                Text(
                    text = "${tenant.rating}$reviewsSuffix",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        val facts = buildList {
            tenant.openNow?.let { open ->
                add(Triple(Icons.Default.Schedule, if (open) "Abierto ahora" else "Cerrado ahora", open))
            }
            if (tenant.schedule.isNotBlank()) add(Triple(Icons.Default.AccessTime, tenant.schedule, true))
            distanceKm?.let { add(Triple(Icons.Default.NearMe, String.format(java.util.Locale.US, "%.1f km", it), true)) }
            tenant.priceFrom?.let { price ->
                val label = if (price == 0.0) "Sin costo" else "Desde " + java.text.NumberFormat
                    .getCurrencyInstance(java.util.Locale.forLanguageTag("es-AR"))
                    .format(price)
                add(Triple(Icons.Default.Payments, label, true))
            }
            if (tenant.durationLabel.isNotBlank()) add(Triple(Icons.Default.Timer, tenant.durationLabel, true))
            if (tenant.available) add(Triple(Icons.Default.EventAvailable, "Disponibilidad informada", true))
            if (tenant.accessible) add(Triple(Icons.AutoMirrored.Filled.Accessible, "Accesible", true))
            tenant.pointsMultiplier?.takeIf { it > 1 }?.let {
                add(Triple(Icons.Default.Stars, String.format(java.util.Locale.US, "%.1fx puntos", it), true))
            }
        }

        if (facts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            facts.forEach { (icon, label, positive) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if (positive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(7.dp))
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (positive) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (tenant.address.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(7.dp))
                Text(tenant.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        if (tenant.areaId.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            SancarlinaChip(
                label = tenant.areaId,
                onClick = {},
                selected = false
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.commerce_about_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = tenant.description.ifBlank { stringResource(R.string.commerce_about_empty) },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (tenant.services.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("Servicios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            tenant.services.take(8).forEach { service ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 5.dp)) {
                    Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(service, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        if (tenant.accessibilityInfo.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("Accesibilidad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            tenant.accessibilityInfo.forEach { item ->
                Text("• $item", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
