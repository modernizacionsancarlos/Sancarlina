package com.sancarlina.app.ui.features.map.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Star
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
import com.sancarlina.app.ui.theme.SancarlinaOnSurface
import com.sancarlina.app.ui.theme.SancarlinaOnSurfaceVariant
import com.sancarlina.app.ui.theme.SancarlinaPrimary

@Composable
fun CommerceInfoCard(
    tenant: Tenant,
    modifier: Modifier = Modifier,
    onRatingClick: () -> Unit = {}
) {
    SancarlinaCard(modifier = modifier) {
        Text(
            text = tenant.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = SancarlinaOnSurface
        )

        if (tenant.industry.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Category,
                    contentDescription = null,
                    tint = SancarlinaPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tenant.industry,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SancarlinaOnSurfaceVariant
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
                    color = SancarlinaPrimary,
                    fontWeight = FontWeight.SemiBold
                )
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
            color = SancarlinaOnSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = tenant.description.ifBlank { stringResource(R.string.commerce_about_empty) },
            style = MaterialTheme.typography.bodyLarge,
            color = SancarlinaOnSurfaceVariant
        )
    }
}
