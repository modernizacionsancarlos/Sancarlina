package com.example.sancarlina.ui.features.home

data class HomeUiState(
    val banners: List<BannerItem> = emptyList(),
    val categories: List<CategoryItem> = emptyList(),
    val nearbyProduct: ProductItem? = null,
    val isLoading: Boolean = false
)

data class BannerItem(
    val title: String,
    val subtitle: String,
    val imageUrl: String
)

data class CategoryItem(
    val name: String,
    val iconName: String // Using Material Symbols Name or custom icon
)

data class ProductItem(
    val name: String,
    val brand: String,
    val price: String, // Though not explicitly in requirement, useful for future
    val phone: String,
    val hasSelloOrigen: Boolean = true
)
