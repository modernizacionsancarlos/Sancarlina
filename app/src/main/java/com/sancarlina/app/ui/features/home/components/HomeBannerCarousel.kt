package com.sancarlina.app.ui.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.sancarlina.app.viewmodel.BannerItem
import kotlinx.coroutines.delay

@Composable
fun HomeBannerCarousel(
    banners: List<BannerItem>,
    onBannerClick: (BannerItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) return

    val count = banners.size
    val pagerState = rememberPagerState(pageCount = { count })

    // Slide automático de locales para "Recomendado para vos" con cambio instantáneo (0ms)
    LaunchedEffect(pagerState, count) {
        if (count > 1) {
            while (true) {
                delay(3200)
                if (!pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % count
                    pagerState.scrollToPage(nextPage) // 0ms / instantáneo
                }
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cardWidth = (maxWidth * 0.86f).coerceAtMost(360.dp)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                pageSpacing = 14.dp,
                contentPadding = PaddingValues(end = (maxWidth - cardWidth).coerceAtLeast(16.dp))
            ) { index ->
                val banner = banners[index]
                HomeHeroCard(
                    banner = banner,
                    badgeIndex = index,
                    onClick = { onBannerClick(banner) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (count > 1) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dotsToShow = count.coerceAtMost(8)
                repeat(dotsToShow) { dotIdx ->
                    val isSelected = (pagerState.currentPage % dotsToShow) == dotIdx
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.5.dp)
                            .size(if (isSelected) 7.dp else 4.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
                            )
                    )
                }
            }
        }
    }
}
