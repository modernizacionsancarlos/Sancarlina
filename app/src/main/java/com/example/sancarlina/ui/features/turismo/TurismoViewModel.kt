package com.example.sancarlina.ui.features.turismo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TurismoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TurismoUiState())
    val uiState: StateFlow<TurismoUiState> = _uiState.asStateFlow()

    init {
        loadTurismoData()
    }

    private fun loadTurismoData() {
        _uiState.update { 
            it.copy(
                categories = listOf("Todos", "Gastronomía", "Cultura", "Hospedaje", "Bodegas"),
                featuredExperience = ExperienceItem(
                    id = "feat-1",
                    title = "Finca La Esencia",
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
                        description = "Descanso y tranquilidad en un entorno natural con servicios de primera clase."
                    ),
                    ExperienceItem(
                        id = "exp-2",
                        title = "El Fogón Ancestral",
                        location = "Ruta de los Sabores",
                        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBLka7HoOnPnFV6YnJWyyMvlP48vQSWb-wgyIOGWycStTZ4UkuBRnN_3Xp5oRwrx-GRoWvsFkVpMRRaRVYmKwWNER-zoXcHQFoSB7JXPkyDdem1EyLJ3ScOuS6OeFH6W9ToSohazcyQ3oUI0LLbAZGPs-psHq2tjRYoKqTTrSkNd3FE_pxh3lntgeHXyp1w-X6w0_iAt7GeCkvlRqBsAc0nA8aOMOcCZcNdVYbSZUrBRJ_DLdBLpHevZJncTbj2rJ1odT4mzTcmPq8",
                        category = "Gastronomía",
                        rating = 4.8f,
                        description = "Sabores auténticos cocinados al fuego de leña. Una propuesta gastronómica única."
                    )
                )
            )
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}
