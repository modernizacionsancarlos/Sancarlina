package com.example.sancarlina.data.model

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
    @get:PropertyName("image_url") @set:PropertyName("image_url")
    var imageUrl: String = "",
    val rating: Double = 0.0,
    @get:PropertyName("reviews_count") @set:PropertyName("reviews_count")
    var reviewsCount: Int = 0
)
