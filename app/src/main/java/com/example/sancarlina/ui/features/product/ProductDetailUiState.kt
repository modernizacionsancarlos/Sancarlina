package com.example.sancarlina.ui.features.product

data class ProductDetailUiState(
    val product: ProductDetail? = null,
    val isLoading: Boolean = false
)

data class ProductDetail(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val imageUrl: String,
    val galleryImages: List<String>,
    val tags: List<String>,
    val phone: String,
    val isFavorite: Boolean = false
)
