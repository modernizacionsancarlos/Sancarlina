package com.sancarlina.app.ui.features.map.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.CommerceMarker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTenantBottomSheetCard(
    marker: CommerceMarker,
    onDismiss: () -> Unit,
    onNavigate: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SancarlinaSurface,
        shape = SancarlinaSheetShape,
        dragHandle = { BottomSheetDefaults.DragHandle(color = SancarlinaOutlineVariant) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (marker.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = marker.imageUrl,
                        contentDescription = marker.name,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = SancarlinaSurfaceContainerHigh
                    ) {}
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = marker.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = SancarlinaOnSurface
                    )
                    if (marker.locationName.isNotBlank()) {
                        Text(
                            text = marker.locationName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SancarlinaOnSurfaceVariant
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = marker.rating.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = SancarlinaOnSurface
                        )
                        if (marker.distance.isNotBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = marker.distance,
                                style = MaterialTheme.typography.labelSmall,
                                color = SancarlinaOutline
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SancarlinaPrimaryButton(
                text = stringResource(R.string.map_view_commerce),
                onClick = onNavigate
            )
        }
    }
}
