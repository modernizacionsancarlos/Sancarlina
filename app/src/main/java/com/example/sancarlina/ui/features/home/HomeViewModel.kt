package com.example.sancarlina.ui.features.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        _uiState.value = HomeUiState(
            banners = listOf(
                BannerItem(
                    title = "OFERTAS",
                    subtitle = "DEL DÍA",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCeoCpk-R2i54OyCev_9a8nISL8N4TfvmTw-qnf4YaN48ZMMhF2NYLR6Zu8N3RegJ0eu_btmYZQdN6_jCsVtBxmIlWdKSMdAXltJuJs8LO-wluOzOE-PSCFeC4yiYKF8QCesAI88_It5-rz_RkiB3rmL0XPG01otSg_7oyTl1VK6lEV9HYj-_F_JSHIlpzfe_BQ41I67IkSRw0eX1HbBh4LGNYWM5HuBjRZRvvA72XzqZd4MosGs7xy49UJGMcIdzZaIutSQryyoSA"
                ),
                BannerItem(
                    title = "COMPRA LOCAL",
                    subtitle = "GANA PUNTOS",
                    imageUrl = ""
                )
            ),
            categories = listOf(
                CategoryItem("PRODUCTOS\nREGIONALES", "prescriptions"),
                CategoryItem("BODEGAS\nY VINOS", "wine_bar"),
                CategoryItem("ARTESANÍAS", "potted_plant"),
                CategoryItem("GASTRONOMÍA", "restaurant"),
                CategoryItem("SERVICIOS", "handyman"),
                CategoryItem("EMPRENDEDORES", "storefront")
            ),
            nearbyProduct = ProductItem(
                id = "miel-1",
                name = "Miel Sancarlina - 1kg",
                brand = "Producción Local",
                price = "$ 4500",
                phone = "5492622000000",
                hasSelloOrigen = true
            )
        )
    }
}
