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
import kotlinx.coroutines.withTimeoutOrNull

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
                // 1. Fetch Tenants con timeout
                val tenants = withTimeoutOrNull(5000) { tenantsRepository.getActiveTenants() } ?: emptyList()
                _uiState.update { it.copy(tenants = tenants) }

                // 2. Fetch Banners
                val bannerSnapshot = withTimeoutOrNull(5000) { firestore.collection("banners").get().await() }
                val banners = if (bannerSnapshot != null && !bannerSnapshot.isEmpty) {
                    bannerSnapshot.documents.map { doc ->
                        BannerItem(
                            title = doc.getString("title") ?: "",
                            subtitle = doc.getString("subtitle") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: ""
                        )
                    }
                } else {
                    getDefaultBanners()
                }

                // 3. Fetch Categories
                val categorySnapshot = withTimeoutOrNull(5000) { firestore.collection("categories").orderBy("order").get().await() }
                val categories = if (categorySnapshot != null && !categorySnapshot.isEmpty) {
                    categorySnapshot.documents.map { doc ->
                        CategoryItem(
                            name = doc.getString("name") ?: "",
                            iconUrl = doc.getString("iconUrl") ?: ""
                        )
                    }
                } else {
                    getDefaultCategories()
                }

                // 4. Fetch Featured Product (Nearby)
                val productSnapshot = withTimeoutOrNull(5000) {
                    firestore.collection("products")
                        .whereEqualTo("featured", true)
                        .limit(1)
                        .get()
                        .await()
                }
                    
                val productDoc = productSnapshot?.documents?.firstOrNull()
                val nearbyProduct = if (productDoc != null) {
                    ProductItem(
                        id = productDoc.id,
                        name = productDoc.getString("name") ?: "",
                        brand = productDoc.getString("brand") ?: "",
                        price = productDoc.getString("price") ?: "",
                        phone = productDoc.getString("phone") ?: ""
                    )
                } else {
                    if (BuildConfig.DEBUG) {
                        ProductItem(
                            id = "default_featured",
                            name = "Miel Pura de Abeja",
                            brand = "Colmenas San Carlos",
                            price = "$3.800"
                        )
                    } else null
                }

                _uiState.update {
                    it.copy(
                        banners = banners,
                        categories = categories,
                        nearbyProduct = nearbyProduct,
                        isLoading = false
                    )
                }

                // Si estamos en debug, sembramos de forma asíncrona para poblar Firestore
                if (BuildConfig.DEBUG && (bannerSnapshot == null || bannerSnapshot.isEmpty)) {
                    seedFirestoreAsync()
                }
            } catch (e: Exception) {
                Logger.e("Error loading home data", e)
                _uiState.update {
                    it.copy(
                        banners = getDefaultBanners(),
                        categories = getDefaultCategories(),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun getDefaultBanners(): List<BannerItem> {
        return listOf(
            BannerItem(
                title = "-20% Dto",
                subtitle = "Ruta del Vino",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuD1KKye6sPJABfxVlO1gK6Pvfbjv6tMRCCOaOcpn9-akT_NN-DanH3l6vJyj6y3rMsJLCWci9kF2gGRwuCGnGnhqukX_Diz4fPzrwGGxQ9OVQVdR5_48sl4WwApzaKf4OsDrbCkXM5YuclXvvkL12d0FZFAcAe7alY9joaX-NDMP5LOOwSfk6buPbiWyJDPi8abiES9UQdReTJzLBRkd_wrF0EgcTQt1qmVf0evDbEwxbBJU2I7h345aOzIVxkPaktyrUjBgJ5J07cg"
            ),
            BannerItem(
                title = "Nuevo",
                subtitle = "Sabores Locales",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCnlkVqOZuqtnjf2iYWEs4griajmiMfTpVzJc5xn3A12ujl_raT9kr-bEvdOxO0Mn2i721TkkEXCJlGS9MReCZBCT6XVGqkDMq9ezRBTAtniPYoWqhgO-tjaicma_YOImVKKYMyCE5Idj95__UDluhBbSKXsTUUNlUeYTzTkjqGAwIC5bO3rS9jFLwGqn5btwNgnuCmE9uRjyxqwFdwhfbNPGb0jsACk1aWUN6sUd3jaLC-3BvoWvo_Oa7VgXva7lPhnQlYBprQ8vwR"
            ),
            BannerItem(
                title = "Escapada",
                subtitle = "Estadías de Campo",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAjV-1Rz1ySHF1DsiSZuEGobN5EBTnYGI6yazVOQxji2lyXTHrPuh0HlYYS-IjU6Ovyh78zJkZp-jryHIPs3Uay1BndaYJSuCpe6rrWI7BplUF9eqzIch8QoKT4ATBfF_EJAIld2f982pctq-3643tIp9ZmRexEbATcGn6I4zXbIIULzJoIw6AEOcaBQ6dzzhp2yBeRiCHoh6ozUlhdWw1SRSXCrg-5Jveq-RI5gjOreWd-u-Q3QPcnYkkX96a3r2kTQ5-ibNCajlm9"
            )
        )
    }

    private fun getDefaultCategories(): List<CategoryItem> {
        return listOf(
            CategoryItem(name = "BODEGAS"),
            CategoryItem(name = "ARTESANÍAS"),
            CategoryItem(name = "GASTRONOMÍA"),
            CategoryItem(name = "ALOJAMIENTO")
        )
    }

    private fun seedFirestoreAsync() {
        viewModelScope.launch {
            try {
                // Verificar si ya existen categorías para no duplicar
                val catSnap = firestore.collection("categories").get().await()
                if (catSnap.isEmpty) {
                    val categories = listOf(
                        mapOf("name" to "BODEGAS", "iconUrl" to "", "order" to 1),
                        mapOf("name" to "ARTESANÍAS", "iconUrl" to "", "order" to 2),
                        mapOf("name" to "GASTRONOMÍA", "iconUrl" to "", "order" to 3),
                        mapOf("name" to "ALOJAMIENTO", "iconUrl" to "", "order" to 4)
                    )
                    categories.forEach { firestore.collection("categories").add(it) }
                }

                val bannerSnap = firestore.collection("banners").get().await()
                if (bannerSnap.isEmpty) {
                    val banners = listOf(
                        mapOf("title" to "-20% Dto", "subtitle" to "Ruta del Vino", "imageUrl" to "https://lh3.googleusercontent.com/aida-public/AB6AXuD1KKye6sPJABfxVlO1gK6Pvfbjv6tMRCCOaOcpn9-akT_NN-DanH3l6vJyj6y3rMsJLCWci9kF2gGRwuCGnGnhqukX_Diz4fPzrwGGxQ9OVQVdR5_48sl4WwApzaKf4OsDrbCkXM5YuclXvvkL12d0FZFAcAe7alY9joaX-NDMP5LOOwSfk6buPbiWyJDPi8abiES9UQdReTJzLBRkd_wrF0EgcTQt1qmVf0evDbEwxbBJU2I7h345aOzIVxkPaktyrUjBgJ5J07cg"),
                        mapOf("title" to "Nuevo", "subtitle" to "Sabores Locales", "imageUrl" to "https://lh3.googleusercontent.com/aida-public/AB6AXuCnlkVqOZuqtnjf2iYWEs4griajmiMfTpVzJc5xn3A12ujl_raT9kr-bEvdOxO0Mn2i721TkkEXCJlGS9MReCZBCT6XVGqkDMq9ezRBTAtniPYoWqhgO-tjaicma_YOImVKKYMyCE5Idj95__UDluhBbSKXsTUUNlUeYTzTkjqGAwIC5bO3rS9jFLwGqn5btwNgnuCmE9uRjyxqwFdwhfbNPGb0jsACk1aWUN6sUd3jaLC-3BvoWvo_Oa7VgXva7lPhnQlYBprQ8vwR"),
                        mapOf("title" to "Escapada", "subtitle" to "Estadías de Campo", "imageUrl" to "https://lh3.googleusercontent.com/aida-public/AB6AXuAjV-1Rz1ySHF1DsiSZuEGobN5EBTnYGI6yazVOQxji2lyXTHrPuh0HlYYS-IjU6Ovyh78zJkZp-jryHIPs3Uay1BndaYJSuCpe6rrWI7BplUF9eqzIch8QoKT4ATBfF_EJAIld2f982pctq-3643tIp9ZmRexEbATcGn6I4zXbIIULzJoIw6AEOcaBQ6dzzhp2yBeRiCHoh6ozUlhdWw1SRSXCrg-5Jveq-RI5gjOreWd-u-Q3QPcnYkkX96a3r2kTQ5-ibNCajlm9")
                    )
                    banners.forEach { firestore.collection("banners").add(it) }
                }
            } catch (e: Exception) {
                Logger.e("Error seeding Firestore", e)
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
