package com.sancarlina.app.ui.features.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sancarlina.app.viewmodel.BannerItem

@Composable
fun HomeBannerCarousel(
    banners: List<BannerItem>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 4.dp)
    ) {
        itemsIndexed(banners, key = { index, banner ->
            "${index}_${banner.title}_${banner.imageUrl}"
        }) { index, banner ->
            HomeHeroCard(banner = banner, badgeIndex = index)
        }
    }
}
