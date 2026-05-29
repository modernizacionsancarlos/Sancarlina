package com.example.sancarlina.ui.features.turismo

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TurismoViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(TurismoUiState())
    val uiState: StateFlow<TurismoUiState> = _uiState.asStateFlow()

    init {
        loadTurismoData()
    }

    private fun loadTurismoData() {
        _uiState.update { it.copy(isLoading = true) }

        firestore.collection("experiences").get()
            .addOnSuccessListener { result ->
                val experiences = result.documents.map { doc ->
                    ExperienceItem(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        location = doc.getString("location") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        category = doc.getString("category") ?: "Cultura",
                        rating = (doc.getDouble("rating") ?: 5.0).toFloat(),
                        description = doc.getString("description") ?: ""
                    )
                }

                if (experiences.isNotEmpty()) {
                    val featured = experiences.firstOrNull { it.id.contains("feat") } ?: experiences.first()
                    _uiState.update { 
                        it.copy(
                            categories = listOf("Todos") + experiences.map { it.category }.distinct(),
                            featuredExperience = featured,
                            experiences = experiences.filter { it.id != featured.id },
                            isLoading = false
                        )
                    }
                } else {
                    loadMockData()
                }
            }
            .addOnFailureListener {
                loadMockData()
            }
    }

    private fun loadMockData() {
        _uiState.update { 
            it.copy(
                categories = listOf("Todos", "Gastronomía", "Cultura", "Hospedaje", "Bodegas"),
                featuredExperience = ExperienceItem(
                    id = "feat-1",
                    title = "Finca La Esencia (Demo)",
                    location = "Valle de Uco, Mendoza",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAGAC6Q7XZc-RLrUnfJpEt4gFQhp52lGxHdAVJ_-_f5nuYa9nms7goZ9FWFI-4Fvvq5FHIhm9_v6-tQwoDpytt9T6u4PRJ_bplpQK_UfUaL_Z_AmDetucGDdt18-DTTL9z5gh3RPs6fMm47pGjeHi26EsPlu8yqHUE59WNZpEuve7KdVz2NtjyakzHvFhedUEU9HbxwxE9ysGHiwyO9aph7ptlB1VtJDl2IMh9FAXCKNIAszxYJJiN05YAM2wUtoImWpuDvYLaHLrM",
                    category = "Bodegas",
                    rating = 4.9f,
                    description = "Experiencia vitivinícola premium con degustación de vinos maduros y gastronomía de autor entre viñedos centenarios."
                ),
                experiences = listOf(
                    ExperienceItem(
                        id = "exp-1",
                        title = "Posada del Sol",
                        location = "Centro Histórico",
                        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBNPTCtbz-ib_ipoATnmhksnha2qDjNTPc5SY-XCFzForLjqg-iyRh6gUIrt7mPOBuHlLxbl2v7pOIDB1gKv7YyHM65MxbqPISOYJb3223FcS4omCSo_mpVKGiZZ1f0l6PiitBREkyNBzCF0lv66HbsSJeV0lnFUJNLEOGHsmNKVtLY0u2c6ebTBeiQAjLQu1WnSMYfdkWDd6Z6Cpd2edrEIijY-Oo6wxQiqw_7thjNbOCF3wtfmPgP9mBDQi-ho655CzkM3LajWC0",
                        category = "Hospedaje",
                        rating = 4.7f,
                        description = "Descanso y tranquilidad en un entorno natural."
                    )
                ),
                isLoading = false
            )
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}
