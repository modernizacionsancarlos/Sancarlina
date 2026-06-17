package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.BuildConfig
import com.sancarlina.app.data.repository.TenantsRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.sancarlina.app.R
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel(
    private val tenantsRepository: TenantsRepository
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    
    // Estado inicial vacío; datos reales desde Firestore (2B-4.1)
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // 1. Fetch Tenants
                val tenants = tenantsRepository.getActiveTenants()
                _uiState.update { it.copy(tenants = tenants) }

                // 2. Fetch Banners
                val bannerSnapshot = firestore.collection("banners").get().await()
                if (!bannerSnapshot.isEmpty) {
                    val banners = bannerSnapshot.documents.map { doc ->
                        BannerItem(
                            title = doc.getString("title") ?: "",
                            subtitle = doc.getString("subtitle") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: ""
                        )
                    }
                    _uiState.update { it.copy(banners = banners) }
                }

                // 3. Fetch Categories
                val categorySnapshot = firestore.collection("categories").orderBy("order").get().await()
                if (!categorySnapshot.isEmpty) {
                    val categories = categorySnapshot.documents.map { doc ->
                        CategoryItem(
                            name = doc.getString("name") ?: "",
                            iconUrl = doc.getString("iconUrl") ?: ""
                        )
                    }
                    _uiState.update { it.copy(categories = categories) }
                }

                // 4. Fetch Featured Product (Nearby)
                val productSnapshot = firestore.collection("products")
                    .whereEqualTo("featured", true)
                    .limit(1)
                    .get()
                    .await()
                    
                val productDoc = productSnapshot.documents.firstOrNull()
                if (productDoc != null) {
                    val product = ProductItem(
                        id = productDoc.id,
                        name = productDoc.getString("name") ?: "",
                        brand = productDoc.getString("brand") ?: "",
                        price = productDoc.getString("price") ?: "",
                        phone = productDoc.getString("phone") ?: ""
                    )
                    _uiState.update { it.copy(nearbyProduct = product, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                Logger.e("Error loading home data", e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Utility to seed initial data — solo DEBUG; reglas Firestore bloquean en release (2B-4.1)
    fun seedFirestore() {
        if (!BuildConfig.DEBUG) return
        val categories = listOf(
            mapOf("name" to "PRODUCTOS\nREGIONALES", "iconUrl" to "https://img.icons8.com/color/512/honey.png", "order" to 1),
            mapOf("name" to "BODEGAS\nY VINOS", "iconUrl" to "https://img.icons8.com/color/512/wine-bottle.png", "order" to 2),
            mapOf("name" to "ARTESANÍAS", "iconUrl" to "https://img.icons8.com/color/512/pottery.png", "order" to 3)
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
