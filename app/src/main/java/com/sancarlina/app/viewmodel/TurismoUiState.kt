package com.sancarlina.app.viewmodel

import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.data.models.displayImageUrl

data class TurismoUiState(
    val banners: List<BannerItem> = emptyList(),
    val points: List<TurismoPoint> = emptyList(),
    val categories: List<String> = listOf("Todos"),
    val selectedCategory: String = "Todos",
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

data class TurismoPoint(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val location: String = "San Carlos",
    val rating: Double = 0.0,
    val phone: String = "",
    val schedule: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

internal fun Tenant.toTurismoPoint(areaName: String = ""): TurismoPoint = TurismoPoint(
    id = id,
    name = name,
    description = description,
    imageUrl = displayImageUrl(),
    category = industry,
    location = areaName.ifBlank { address.ifBlank { "San Carlos" } },
    rating = rating,
    phone = contactPhone,
    schedule = schedule,
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0
)
