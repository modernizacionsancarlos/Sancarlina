package com.sancarlina.app.data.models

import com.google.firebase.firestore.PropertyName

data class Tenant(
    val id: String = "",
    val name: String = "",
    val industry: String = "",
    val status: String = "",
    @get:PropertyName("geo_coordinates") @set:PropertyName("geo_coordinates")
    var geoCoordinates: String = "",
    @get:PropertyName("area_id") @set:PropertyName("area_id")
    var areaId: String = "",
    @get:PropertyName("cover_url") @set:PropertyName("cover_url")
    var coverUrl: String = "",
    val description: String = "",
    @get:PropertyName("contact_email") @set:PropertyName("contact_email")
    var contactEmail: String = "",
    @get:PropertyName("contact_phone") @set:PropertyName("contact_phone")
    var contactPhone: String = "",
    val address: String = "",
    val gallery: List<String> = emptyList(),
    @get:PropertyName("image_url") @set:PropertyName("image_url")
    var imageUrl: String = "",
    @get:PropertyName("photo_url") @set:PropertyName("photo_url")
    var photoUrl: String = "",
    @get:PropertyName("rating") @set:PropertyName("rating")
    var ratingValue: Any = 0.0,
    @get:PropertyName("reviews_count") @set:PropertyName("reviews_count")
    var reviewsCount: Int = 0
) {
    val rating: Double
        get() = when (val r = ratingValue) {
            is Number -> r.toDouble()
            is String -> r.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }

    val area_id: String get() = areaId
    val geo_coordinates: String get() = geoCoordinates
    val cover_url: String get() = coverUrl
    val contact_email: String get() = contactEmail
    val contact_phone: String get() = contactPhone
    val image_url: String get() = imageUrl
    val reviews_count: Int get() = reviewsCount
}

fun Tenant.displayImageUrl(): String {
    return imageUrl.ifBlank { coverUrl }.ifBlank { photoUrl }
}
