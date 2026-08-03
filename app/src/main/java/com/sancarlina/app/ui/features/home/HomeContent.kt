package com.sancarlina.app.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sancarlina.app.R
import com.sancarlina.app.ui.features.home.components.*
import com.sancarlina.app.ui.theme.SancarlinaBackground
import com.sancarlina.app.ui.theme.SancarlinaPrimary
import com.sancarlina.app.ui.theme.SancarlinaTheme
import com.sancarlina.app.viewmodel.BannerItem
import com.sancarlina.app.viewmodel.CategoryItem
import com.sancarlina.app.viewmodel.HomeUiState
import com.sancarlina.app.viewmodel.HomeViewModel
import com.sancarlina.app.viewmodel.ProductItem

@Composable
fun HomeContent(
    viewModel: HomeViewModel = viewModel(),
    uiStateOverride: HomeUiState? = null,
    onNavigateToCategory: (String) -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToNews: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onOpenDrawer: () -> Unit = {}
) {
    val collectedState by viewModel.uiState.collectAsState()
    HomeContentBody(
        uiState = uiStateOverride ?: collectedState,
        onNavigateToCategory = onNavigateToCategory,
        onNavigateToSearch = onNavigateToSearch,
        onNavigateToNews = onNavigateToNews,
        onNavigateToDetail = onNavigateToDetail
    )
}

@Composable
internal fun HomeContentBody(
    uiState: HomeUiState,
    onNavigateToCategory: (String) -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToNews: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
    ) {
        if (uiState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = SancarlinaPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            HomeWelcomeHeader()
            Spacer(modifier = Modifier.height(16.dp))

            HomeSearchBar(onClick = onNavigateToSearch)
            Spacer(modifier = Modifier.height(20.dp))

            HomeSectionHeader(
                title = stringResource(R.string.home_offers_section),
                actionLabel = stringResource(R.string.home_see_all),
                onActionClick = onNavigateToNews
            )
            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isLoading && uiState.banners.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SancarlinaPrimary)
                    }
                }
                uiState.banners.isEmpty() -> {
                    HomeEmptySection(
                        message = stringResource(R.string.home_banners_empty),
                        icon = Icons.Outlined.Newspaper
                    )
                }
                else -> {
                    HomeBannerCarousel(banners = uiState.banners)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            HomeSectionHeader(title = stringResource(R.string.home_explore_section))
            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isLoading && uiState.categories.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SancarlinaPrimary)
                    }
                }
                uiState.categories.isEmpty() -> {
                    HomeEmptySection(
                        message = stringResource(R.string.home_categories_empty),
                        icon = Icons.Outlined.Category
                    )
                }
                else -> {
                    HomeCategoryGrid(
                        categories = uiState.categories,
                        onCategoryClick = { onNavigateToCategory(it.name) }
                    )
                }
            }

            uiState.nearbyProduct?.let { product ->
            Spacer(modifier = Modifier.height(20.dp))
                HomeSectionHeader(title = stringResource(R.string.home_featured_section))
                Spacer(modifier = Modifier.height(8.dp))
                HomeFeaturedProductCard(
                    product = product,
                    onClick = { onNavigateToDetail(product.id) }
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeContentPreview() {
    SancarlinaTheme {
        HomeContentBody(uiState = previewHomeUiState())
    }
}

/** Datos solo para @Preview; no se usan en release. */
private fun previewHomeUiState() = HomeUiState(
    isLoading = false,
    banners = listOf(
        BannerItem(title = "Nuevo", subtitle = "Ruta del Vino", imageUrl = ""),
        BannerItem(title = "-20% Dto", subtitle = "Sabores Locales", imageUrl = "")
    ),
    categories = listOf(
        CategoryItem(name = "BODEGAS\nY VINOS"),
        CategoryItem(name = "ARTESANÍAS"),
        CategoryItem(name = "GASTRONOMÍA"),
        CategoryItem(name = "ALOJAMIENTO")
    ),
    nearbyProduct = ProductItem(
        id = "preview",
        name = "Malbec Reserva",
        brand = "Bodega ejemplo",
        price = "$4.500"
    )
)
