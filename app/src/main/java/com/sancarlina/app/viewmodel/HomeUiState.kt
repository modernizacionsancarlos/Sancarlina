package com.sancarlina.app.viewmodel

import com.sancarlina.app.data.models.Tenant

data class HomeUiState(
    val banners: List<BannerItem> = emptyList(),
    val categories: List<CategoryItem> = emptyList(),
    val nearbyProduct: ProductItem? = null,
    val tenants: List<Tenant> = emptyList(),
    val isLoading: Boolean = false
)

data class BannerItem(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val imageUrl: String = "",
    val content: String = "",
    val date: String = "",
    val tag: String = "",
    val authorName: String = "",
    val authorRole: String = "",
    val authorImageUrl: String = "",
    val readingTime: String = "3 min de lectura"
)

data class CategoryItem(
    val name: String = "",
    val iconUrl: String = "",
    val iconRes: Int? = null
)

data class ProductItem(
    val id: String = "",
    val name: String = "",
    val brand: String = "",
    val price: String = "",
    val phone: String = "",
    val imageUrl: String = "",
    val hasSelloOrigen: Boolean = true
)
