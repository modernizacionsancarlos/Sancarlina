package com.sancarlina.app.ui.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.BannerItem

@Composable
fun HomeHeroCard(
    banner: BannerItem,
    modifier: Modifier = Modifier,
    badgeIndex: Int = 0
) {
    val badgeColor = when (badgeIndex % 3) {
        1 -> SancarlinaSecondary
        2 -> SancarlinaTertiary
        else -> SancarlinaPrimary
    }
    val badgeText = when {
        banner.title.isNotBlank() -> banner.title
        banner.subtitle.isNotBlank() -> banner.subtitle
        else -> ""
    }

    Surface(
        modifier = modifier
            .height(196.dp),
        shape = SancarlinaCardShape,
        shadowElevation = 4.dp,
        color = SancarlinaSurfaceContainerLow
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (banner.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = banner.imageUrl,
                    contentDescription = banner.subtitle.ifBlank { banner.title },
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SancarlinaSurfaceContainerHigh)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 80f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                if (badgeText.isNotBlank()) {
                    Surface(
                        color = badgeColor.copy(alpha = 0.9f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = badgeText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                val headline = banner.subtitle.ifBlank { banner.title }
                if (headline.isNotBlank()) {
                    Text(
                        text = headline,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
