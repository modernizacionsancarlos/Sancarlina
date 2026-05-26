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
    
    // Default state with Premium Illustrative Icons matching the requested aesthetic
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
                CategoryItem(
                    "PRODUCTOS\nREGIONALES", 
                    "https://lh3.googleusercontent.com/aida-public/AB6AXuBO-zCbcsNCC2scnOIN4pWhL6y4EXC8DB8kozNHGOfapsQSChWI9tpAqFHzamfrcaRTLQ1luw689jxdB5Qd6T4BiQxmi2udVClm4tFOQFdVlwZp5Nv8G4RieGgZ4PDU624OHl0-ymlL7qzjgg7CVcxXDeMNVA2OuGjLfnP6K-5iG7xmLQoWV_21QUqXu9YSIAfqnuauPRmukhPqmz4uk9IHWTqoiPjEQR7qijlzg9mb0QKOcMnB9UJCIPCSLS3pC-_w4ev74l_5BSo"
                ),
                CategoryItem(
                    "BODEGAS\nY VINOS", 
                    "https://lh3.googleusercontent.com/aida-public/AB6AXuAaRawZWDnHG1z12w270gKPsi8Tnj5iO0HexCUOQ2_1tCMa-3BFniZRFsj6IBsEmWkTGFUp0rH3M11Hwg6rzLLtBAWwEkrSFMFNPm8ydbMSwfzTolzPR96AZPowv5gZNIommIAbQm-7xsi-LhI9BtSsZD6xqiFF4-2tChqUGsXhitwykVv9pvI8mjhz03qxss3E-hmj8Ggcyct33e2zFpiIVA7jBfqEszqSZewoq2M41MvFuyTvkEXLPHKEQ6w96owDshdzu5UYd_w"
                ),
                CategoryItem(
                    "ARTESANÍAS", 
                    "https://lh3.googleusercontent.com/aida-public/AB6AXuC-JzLy0T0x03mi-Dz-tDSBj8W7Z6IWSd_vTaXOkRXefQq1BuB86rhJnoWcp7hrUP17h1gtJSjI66HJadhtPGYKDSStAE6fJSBf2CfeCJ5xlcB6knY6SCRBu-hIYE23ti1-cGSbhGhrGlzVfFmOCpHqkfKLeVBc45LiSzu7QzX2J0rIurfrnAUAO8bXXE5AubTq6vW8q7GgZA1XbbuKLh8CdWP9Zfdhw1-k-pJ6FGSQKH0slKRMpNXHUwGbbC2B8zW7wiBTg1LZ3_w"
                ),
                CategoryItem(
                    "GASTRONOMÍA", 
                    "https://lh3.googleusercontent.com/aida-public/AB6AXuCmWD2p8jAw-TKUyaqxqMQyECjRSddGvzI-FPYpBaJpqo-KHAXWe4YOfGoIIjIYWK_x-qSCXvf21kC8QgrOmYiMZEgSmFM0s8U2LVO2IZlj4OHCa6d3LoVRlDuO7Y4l8tGkPbWdIKKRelsaQ1l0HEOd5UR21G-2rSwCf6zDENeTkn-OY-db1iq9zf26r_eBUqpupf6uzkk8Ad6HHzCyWRoNXcUFUv_M6NfbJGkxAIOAX5riYsr01gYlXCwDZcn1IXly6eLjFUGU6ng"
                ),
                CategoryItem(
                    "SERVICIOS", 
                    "https://lh3.googleusercontent.com/aida-public/AB6AXuD469EJ0ASKwa3RxbWJHv3zmP98xE1PcrSkhY5Rb_MW_Ps8F5cNCYRtgirKoBtTy1zw9kJBrVcjA_X2qGRKoSq0-t7zg35bHk7VagxD9B7ULBCBMRIBny8Aa3sb3PB_Z1vYUM3P29xx4f3jYORkO_Yu3rnvi7LKwidDceaSAsKsSRPXYP6YEUcqPykSKEImuD9h_u-OkX5ozAvcbdJJcsi81vLjoBw2eHawMA0XktHh-Cfy0l-grZTeNpgm3gu955mA5ktFbGpRBpE"
                ),
                CategoryItem(
                    "EMPRENDEDORES", 
                    "https://lh3.googleusercontent.com/aida-public/AB6AXuDsABedTp8Cg8fdlL_aaJKsyAMQ1dgqv6i6uGwEUX6iwcTTg86huLPk9yOmrjSedvO94iVOt2GVx1A-ARWuTq-KPaJHuKDKDEerBefyHkl86SlRR0fgVQIgJdzra9njMTsl2dfxd6_zDTZyhhxxKrE2ti9lfUw1-ryY21pZstrP0uqFpf6I38VVH7uQWvlhOJ_bniSFbO9bJKS5Xh-Xo5UqUbi9GFZ__KaE0I4lZrOAdhDaOLbgmqYu5ogNKA0lYy51J7i8JHSoIj8"
                )
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
                            iconUrl = doc.getString("iconUrl") ?: ""
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
            mapOf("name" to "PRODUCTOS\nREGIONALES", "iconUrl" to "https://lh3.googleusercontent.com/aida-public/AB6AXuBO-zCbcsNCC2scnOIN4pWhL6y4EXC8DB8kozNHGOfapsQSChWI9tpAqFHzamfrcaRTLQ1luw689jxdB5Qd6T4BiQxmi2udVClm4tFOQFdVlwZp5Nv8G4RieGgZ4PDU624OHl0-ymlL7qzjgg7CVcxXDeMNVA2OuGjLfnP6K-5iG7xmLQoWV_21QUqXu9YSIAfqnuauPRmukhPqmz4uk9IHWTqoiPjEQR7qijlzg9mb0QKOcMnB9UJCIPCSLS3pC-_w4ev74l_5BSo", "order" to 1),
            mapOf("name" to "BODEGAS\nY VINOS", "iconUrl" to "https://lh3.googleusercontent.com/aida-public/AB6AXuAaRawZWDnHG1z12w270gKPsi8Tnj5iO0HexCUOQ2_1tCMa-3BFniZRFsj6IBsEmWkTGFUp0rH3M11Hwg6rzLLtBAWwEkrSFMFNPm8ydbMSwfzTolzPR96AZPowv5gZNIommIAbQm-7xsi-LhI9BtSsZD6xqiFF4-2tChqUGsXhitwykVv9pvI8mjhz03qxss3E-hmj8Ggcyct33e2zFpiIVA7jBfqEszqSZewoq2M41MvFuyTvkEXLPHKEQ6w96owDshdzu5UYd_w", "order" to 2),
            mapOf("name" to "ARTESANÍAS", "iconUrl" to "https://lh3.googleusercontent.com/aida-public/AB6AXuC-JzLy0T0x03mi-Dz-tDSBj8W7Z6IWSd_vTaXOkRXefQq1BuB86rhJnoWcp7hrUP17h1gtJSjI66HJadhtPGYKDSStAE6fJSBf2CfeCJ5xlcB6knY6SCRBu-hIYE23ti1-cGSbhGhrGlzVfFmOCpHqkfKLeVBc45LiSzu7QzX2J0rIurfrnAUAO8bXXE5AubTq6vW8q7GgZA1XbbuKLh8CdWP9Zfdhw1-k-pJ6FGSQKH0slKRMpNXHUwGbbC2B8zW7wiBTg1LZ3_w", "order" to 3)
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
