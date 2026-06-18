package com.sancarlina.app.ui.features.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.ui.components.SancarlinaElevatedCard
import com.sancarlina.app.ui.theme.SancarlinaOnSurfaceVariant
import com.sancarlina.app.ui.theme.SancarlinaPrimary
import com.sancarlina.app.viewmodel.ProductItem

@Composable
fun HomeFeaturedProductCard(
    product: ProductItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SancarlinaElevatedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = SancarlinaPrimary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (product.brand.isNotBlank()) {
                    Text(
                        text = product.brand,
                        style = MaterialTheme.typography.labelMedium,
                        color = SancarlinaOnSurfaceVariant
                    )
                }
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (product.price.isNotBlank()) {
                    Text(
                        text = product.price,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SancarlinaPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
