package com.sancarlina.app.ui.features.category.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sancarlina.app.R
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.CommerceMarker

@Composable
fun CommerceListCard(
    commerce: CommerceMarker,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = SancarlinaCardShape,
        color = SancarlinaSurfaceContainerLowest,
        shadowElevation = 3.dp,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (commerce.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = commerce.imageUrl,
                    contentDescription = commerce.name,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(96.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = SancarlinaSurfaceContainerHigh
                ) {}
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 4.dp)
            ) {
                Text(
                    text = commerce.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SancarlinaOnSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (commerce.locationName.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = SancarlinaOutline,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = commerce.locationName,
                            style = MaterialTheme.typography.labelMedium,
                            color = SancarlinaOnSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = commerce.rating.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface
                    )
                    if (commerce.distance.isNotBlank()) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = commerce.distance,
                            style = MaterialTheme.typography.labelSmall,
                            color = SancarlinaOutline
                        )
                    }
                }
            }

            IconButton(onClick = { /* favorito pendiente */ }) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    stringResource(R.string.cd_favorite),
                    tint = SancarlinaSecondary
                )
            }
        }
    }
}

/** Alias de compatibilidad para pantallas que aún importan CommerceCard. */
@Composable
fun CommerceCard(commerce: CommerceMarker, onClick: () -> Unit) {
    CommerceListCard(commerce = commerce, onClick = onClick)
}
