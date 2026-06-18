package com.sancarlina.app.ui.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val imageUrl: String
)

@Composable
fun OnboardingContent(onFinish: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            title = "Descubre lo mejor de San Carlos",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB7NmUMx77pRwXnAJE5bIhfBdYsy4v2zr_nW-zGfckE9bqUHb6r0j2_VWFglDf8wvd6EeepsxgpAkHS1GMxmnO5ljfRzRy-Vvovfl56liRO0A9gf9OUSUKVSJeqnye_TH31p_JqUL1TKclszf0Rt10j9K6EH5CmH3vgQ_gaN2OT1UEYITHViTUxv3M6QP4h1aKl1EajQUpk-lt78R5GRcrh_q_HjzVYVKMti4wvzaClFquhc1dxlefXL_AXTAaAD3SWUke3nZVC74Qo"
        ),
        OnboardingPage(
            title = "Encuentra productos regionales",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuD1KKye6sPJABfxVlO1gK6Pvfbjv6tMRCCOaOcpn9-akT_NN-DanH3l6vJyj6y3rMsJLCWci9kF2gGRwuCGnGnhqukX_Diz4fPzrwGGxQ9OVQVdR5_48sl4WwApzaKf4OsDrbCkXM5YuclXvvkL12d0FZFAcAe7alY9joaX-NDMP5LOOwSfk6buPbiWyJDPi8abiES9UQdReTJzLBRkd_wrF0EgcTQt1qmVf0evDbEwxbBJU2I7h345aOzIVxkPaktyrUjBgJ5J07cg"
        ),
        OnboardingPage(
            title = "Suma puntos con tus compras",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCnlkVqOZuqtnjf2iYWEs4griajmiMfTpVzJc5xn3A12ujl_raT9kr-bEvdOxO0Mn2i721TkkEXCJlGS9MReCZBCT6XVGqkDMq9ezRBTAtniPYoWqhgO-tjaicma_YOImVKKYMyCE5Idj95__UDluhBbSKXsTUUNlUeYTzTkjqGAwIC5bO3rS9jFLwGqn5btwNgnuCmE9uRjyxqwFdwhfbNPGb0jsACk1aWUN6sUd3jaLC-3BvoWvo_Oa7VgXva7lPhnQlYBprQ8vwR"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { index ->
            val page = pages[index]
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = page.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    SancarlinaOnSurface.copy(alpha = 0.4f),
                                    SancarlinaOnSurface.copy(alpha = 0.9f)
                                ),
                                startY = 400f
                            )
                        )
                )

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = SancarlinaSurfaceContainerLowest,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 140.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.height(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(pages.size) { iteration ->
                    val isActive = pagerState.currentPage == iteration
                    val color = if (isActive) {
                        SancarlinaPrimaryFixedDim
                    } else {
                        SancarlinaSurfaceContainerLowest.copy(alpha = 0.4f)
                    }
                    val width = if (isActive) 32.dp else 8.dp
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color)
                            .size(width = width, height = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            SancarlinaPrimaryButton(
                text = if (pagerState.currentPage == pages.size - 1) {
                    stringResource(R.string.onboarding_start).uppercase()
                } else {
                    stringResource(R.string.onboarding_next).uppercase()
                },
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onFinish()
                    }
                }
            )
        }
    }
}
