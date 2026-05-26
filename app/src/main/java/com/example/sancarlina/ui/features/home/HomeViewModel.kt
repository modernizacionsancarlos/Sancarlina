package com.example.sancarlina.ui.features.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

class HomeViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    
    // Default state with Mock data to ensure the screen is NEVER empty
    private val _uiState = MutableStateFlow(
        HomeUiState(
            banners = listOf(
                BannerItem(
                    title = "OFERTAS",
                    subtitle = "DEL DÍA",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCeoCpk-R2i54OyCev_9a8nISL8N4TfvmTw-qnf4YaN48ZMMhF2NYLR6Zu8N3RegJ0eu_btmYZQdN6_jCsVtBxmIlWdKSMdAXltJuJs8LO-wluOzOE-PSCFeC4yiYKF8QCesAI88_It5-rz_RkiB3rmL0XPG01otSg_7oyTl1VK6lEV9HYj-_F_JSHIlpzfe_BQ41I67IkSRw0eX1HbBh4LGNYWM5HuBjRZRvvA72XzqZd4MosGs7xy49UJGMcIdzZaIutSQryyoSA"
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
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        _uiState.update { it.copy(isLoading = true) }

        // Fetch Banners
        firestore.collection("banners").get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val banners = result.documents.map { doc ->
                        BannerItem(
                            title = doc.getString("title") ?: "",
                            subtitle = doc.getString("subtitle") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: ""
                        )
                    }
                    _uiState.update { it.copy(banners = banners) }
                }
            }

        // Fetch Categories
        firestore.collection("categories").orderBy("order").get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val categories = result.documents.map { doc ->
                        CategoryItem(
                            name = doc.getString("name") ?: "",
                            iconName = doc.getString("iconName") ?: ""
                        )
                    }
                    _uiState.update { it.copy(categories = categories) }
                }
            }

        // Fetch Featured Product (Nearby)
        firestore.collection("products").whereEqualTo("featured", true).limit(1).get()
            .addOnSuccessListener { result ->
                val doc = result.documents.firstOrNull()
                if (doc != null) {
                    val product = ProductItem(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        brand = doc.getString("brand") ?: "",
                        price = doc.getString("price") ?: "",
                        phone = doc.getString("phone") ?: ""
                    )
                    _uiState.update { it.copy(nearbyProduct = product, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(isLoading = false) }
            }
    }

    // Utility to seed initial data if needed
    fun seedFirestore() {
        val categories = listOf(
            mapOf("name" to "PRODUCTOS\nREGIONALES", "iconName" to "prescriptions", "order" to 1),
            mapOf("name" to "BODEGAS\nY VINOS", "iconName" to "wine_bar", "order" to 2),
            mapOf("name" to "ARTESANÍAS", "iconName" to "potted_plant", "order" to 3)
        )
        categories.forEach { firestore.collection("categories").add(it) }

        val sampleCommerce = mapOf(
            "name" to "Bodega La Celia",
            "category" to "BODEGAS\nY VINOS",
            "locationName" to "Eugenio Bustos",
            "position" to GeoPoint(-33.7667, -69.1000),
            "phone" to "5492622000001",
            "imageUrl" to "https://lh3.googleusercontent.com/aida-public/AB6AXuDqRRXzgOsXEGjd7hU4sp0ogAWj6ziCETn5orGxGaxDrds4rajh2-Oq1xI_zo1E6Qbw39ZiwJRXQaViytdfnpj-iK57JI5Ka57vnmIGZ-5c-mS0RysfSO68JxDxhTxpuUzsW-j1a8Huvco3qhtfIncJdyjjH3EDvM5RY2ssw5MAaPOedn84EqcOryHb0VYn46dXKzbqDPuf8uOqHKEJoKMqmcZ0D18ZaDlFHbVRT2v-e9a-KR2pSkHDBfgKF9ril7fFuLfhytDtyPw",
            "rating" to 4.9,
            "distance" to "A 1.2 km de vos"
        )
        firestore.collection("commerces").add(sampleCommerce)
    }
}
