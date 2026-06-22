package com.sancarlina.app.ui.features.product.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaChip
import com.sancarlina.app.ui.features.product.ProductDetail
import com.sancarlina.app.ui.theme.*

@Composable
fun ProductInfoSection(
    product: ProductDetail,
    modifier: Modifier = Modifier
) {
    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }

    if (selectedImageIndex != null) {
        ImageGalleryDialog(
            images = product.galleryImages,
            initialIndex = selectedImageIndex!!,
            productName = product.name,
            onDismiss = { selectedImageIndex = null }
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (product.tags.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(product.tags) { tag ->
                    SancarlinaChip(label = tag, onClick = {}, selected = false)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = SancarlinaOnBackground
        )

        if (product.location.isNotBlank()) {
            Text(
                text = product.location,
                style = MaterialTheme.typography.bodyLarge,
                color = SancarlinaOnSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (product.price.isNotBlank()) {
            Text(
                text = product.price,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = SancarlinaPrimary,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 20.dp),
            color = SancarlinaOutlineVariant.copy(alpha = 0.3f)
        )

        Text(
            text = stringResource(R.string.product_description_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            color = SancarlinaOnBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = product.description.ifBlank { stringResource(R.string.product_description_empty) },
            style = MaterialTheme.typography.bodyLarge,
            color = SancarlinaOnSurfaceVariant
        )

        if (product.galleryImages.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.product_gallery_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = SancarlinaOnBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(product.galleryImages) { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(SancarlinaCardShape)
                            .clickable { selectedImageIndex = product.galleryImages.indexOf(imageUrl) },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
