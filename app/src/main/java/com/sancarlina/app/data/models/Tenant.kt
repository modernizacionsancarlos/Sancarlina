package com.sancarlina.app.data.models

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.GeoPoint

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
    @get:PropertyName("logo_url") @set:PropertyName("logo_url")
    var logoUrl: String = "",
    val schedule: String = "",
    val website: String = "",
    val whatsapp: String = "",
    val services: List<String> = emptyList(),
    val accessibilityInfo: List<String> = emptyList(),
    val durationLabel: String = "",
    val priceFrom: Double? = null,
    val openNow: Boolean? = null,
    val available: Boolean = false,
    val accessible: Boolean = false,
    val pointsMultiplier: Double? = null,
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
    val logo_url: String get() = logoUrl
    val reviews_count: Int get() = reviewsCount

    val latitude: Double?
        get() = geoCoordinates.split(",").getOrNull(0)?.trim()?.toDoubleOrNull()

    val longitude: Double?
        get() = geoCoordinates.split(",").getOrNull(1)?.trim()?.toDoubleOrNull()

    companion object {
        /**
         * Normaliza el documento compartido con la Web. Firestore contiene datos de
         * distintas etapas del proyecto (camelCase, snake_case y algunos GeoPoint),
         * por lo que deserializarlo directamente puede ocultar campos válidos.
         */
        fun fromMap(id: String, data: Map<String, Any?>): Tenant {
            fun value(vararg keys: String): Any? = keys.firstNotNullOfOrNull { data[it] }
            fun text(vararg keys: String): String = value(*keys)?.toString()?.trim().orEmpty()
            fun number(vararg keys: String): Number? = when (val raw = value(*keys)) {
                is Number -> raw
                is String -> raw.toDoubleOrNull()
                else -> null
            }
            fun strings(vararg keys: String): List<String> = when (val raw = value(*keys)) {
                is List<*> -> raw.mapNotNull { it?.toString()?.trim()?.takeIf(String::isNotBlank) }
                is String -> listOf(raw).filter(String::isNotBlank)
                else -> emptyList()
            }
            fun boolean(vararg keys: String): Boolean? = when (val raw = value(*keys)) {
                is Boolean -> raw
                is String -> when (raw.trim().lowercase()) {
                    "true", "1", "yes", "si", "sí", "open", "abierto" -> true
                    "false", "0", "no", "closed", "cerrado" -> false
                    else -> null
                }
                is Number -> raw.toInt() != 0
                else -> null
            }

            fun extractUrl(vararg keys: String): String {
                val raw = value(*keys) ?: return ""
                val extracted = when (raw) {
                    is String -> raw.trim()
                    is Map<*, *> -> (raw["url"] ?: raw["downloadURL"] ?: raw["src"] ?: raw["uri"] ?: raw["link"])?.toString()?.trim().orEmpty()
                    is List<*> -> {
                        val first = raw.firstOrNull()
                        when (first) {
                            is String -> first.trim()
                            is Map<*, *> -> (first["url"] ?: first["downloadURL"] ?: first["src"] ?: first["uri"] ?: first["link"])?.toString()?.trim().orEmpty()
                            else -> first?.toString()?.trim().orEmpty()
                        }
                    }
                    else -> raw.toString().trim()
                }
                if (extracted.startsWith("{") && extracted.contains("url=")) {
                    val match = Regex("url=([^,}]+)").find(extracted)
                    if (match != null) return match.groupValues[1].trim()
                }
                return extracted
            }

            fun extractUrlList(vararg keys: String): List<String> {
                val raw = value(*keys) ?: return emptyList()
                return when (raw) {
                    is List<*> -> raw.mapNotNull { item ->
                        when (item) {
                            is String -> item.trim().takeIf(String::isNotBlank)
                            is Map<*, *> -> (item["url"] ?: item["downloadURL"] ?: item["src"] ?: item["uri"] ?: item["link"])?.toString()?.trim()?.takeIf(String::isNotBlank)
                            else -> item?.toString()?.trim()?.takeIf(String::isNotBlank)
                        }
                    }
                    is String -> if (raw.isNotBlank()) listOf(raw.trim()) else emptyList()
                    is Map<*, *> -> {
                        val u = (raw["url"] ?: raw["downloadURL"] ?: raw["src"] ?: raw["uri"] ?: raw["link"])?.toString()?.trim()
                        if (!u.isNullOrBlank()) listOf(u) else emptyList()
                    }
                    else -> emptyList()
                }
            }

            val rawCoordinates = value("geo_coordinates", "geoCoordinates", "position", "coordinates")
            val coordinates = when (rawCoordinates) {
                is GeoPoint -> "${rawCoordinates.latitude},${rawCoordinates.longitude}"
                is Map<*, *> -> {
                    val lat = rawCoordinates["latitude"] ?: rawCoordinates["lat"]
                    val lng = rawCoordinates["longitude"] ?: rawCoordinates["lng"]
                    if (lat != null && lng != null) "$lat,$lng" else ""
                }
                null -> {
                    val lat = value("latitude", "lat")
                    val lng = value("longitude", "lng", "lon")
                    if (lat != null && lng != null) "$lat,$lng" else ""
                }
                else -> rawCoordinates.toString().trim()
            }

            return Tenant(
                id = id,
                name = text("name", "title", "nombre"),
                industry = text("industry", "category", "type", "rubro", "categoria"),
                status = text("status", "estado").ifBlank { "active" },
                geoCoordinates = coordinates,
                areaId = text("area_id", "areaId", "area"),
                coverUrl = extractUrl("cover_url", "coverUrl", "cover", "cover_image", "coverImage", "portada", "portada_url", "banner", "banner_url", "header_image", "headerImage"),
                description = text("short_description", "description", "about", "bio", "descripcion"),
                contactEmail = text("contact_email", "contactEmail", "email", "correo"),
                contactPhone = text("contact_phone", "contactPhone", "phone", "phone_number", "contact_phone_number", "telefono"),
                address = text("address", "location", "locality", "city", "zone", "direccion"),
                gallery = extractUrlList("gallery", "gallery_urls", "images", "imagenes", "photos", "fotos", "pictures"),
                imageUrl = extractUrl("image_url", "imageUrl", "image", "imagen", "imagen_url", "picture_url", "picture", "img", "url"),
                photoUrl = extractUrl("photo_url", "photoUrl", "photo", "foto", "foto_url", "avatar", "thumbnail"),
                logoUrl = extractUrl("logo_url", "logoUrl", "logo", "icon", "icono"),
                schedule = text("schedule", "opening_hours", "hours", "horarios"),
                website = text("website", "web", "site_url", "sitio_web"),
                whatsapp = text("whatsapp", "whatsapp_number", "contact_whatsapp"),
                services = strings("services", "products", "catalog", "offerings", "servicios", "productos"),
                accessibilityInfo = strings("accessibility", "accessibility_info"),
                durationLabel = text("duration", "duration_label", "estimated_duration"),
                priceFrom = number("price_from", "price", "starting_price", "cost", "precio")?.toDouble(),
                openNow = boolean("open_now", "is_open", "openNow")
                    ?: when (text("open_status", "status_today").lowercase()) {
                        "open", "abierto" -> true
                        "closed", "cerrado" -> false
                        else -> null
                    },
                available = boolean("available", "available_today", "booking_available") == true,
                accessible = boolean("accessible", "wheelchair_accessible") == true
                    || strings("accessibility", "accessibility_info")
                        .joinToString(" ")
                        .contains(Regex("acces|silla|wheelchair|rampa", RegexOption.IGNORE_CASE)),
                pointsMultiplier = number("points_multiplier", "benefit_multiplier")?.toDouble(),
                ratingValue = value("rating", "avg_rating", "averageRating") ?: 0.0,
                reviewsCount = number("reviews_count", "review_count", "ratings_count")?.toInt() ?: 0
            )
        }
    }
}

fun Tenant.displayImageUrl(): String {
    val candidate = coverUrl.ifBlank { imageUrl }
        .ifBlank { photoUrl }
        .ifBlank { gallery.firstOrNull { it.isNotBlank() }.orEmpty() }
        .ifBlank { logoUrl }
        .trim()

    if (candidate.isNotBlank() && (candidate.startsWith("http://") || candidate.startsWith("https://") || candidate.startsWith("data:image/") || candidate.startsWith("content://"))) {
        return candidate
    }

    return fallbackSanCarlosImage(industry)
}

fun fallbackSanCarlosImage(industry: String): String {
    val ind = industry.lowercase()
    return when {
        ind.contains("vino") || ind.contains("bodega") || ind.contains("vitivin") ->
            "https://images.unsplash.com/photo-1506377247377-2a5b3b417ebb?auto=format&fit=crop&w=1200&q=80"
        ind.contains("gastronom") || ind.contains("restauran") || ind.contains("comida") || ind.contains("caf") || ind.contains("bar") ->
            "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1200&q=80"
        ind.contains("turismo") || ind.contains("aventura") || ind.contains("trekking") || ind.contains("excurs") || ind.contains("paisaje") || ind.contains("montaña") ->
            "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=1200&q=80"
        ind.contains("artesan") || ind.contains("comercio") || ind.contains("local") || ind.contains("tienda") || ind.contains("ceram") ->
            "https://images.unsplash.com/photo-1513519245088-0e12902e5a38?auto=format&fit=crop&w=1200&q=80"
        ind.contains("hosped") || ind.contains("hotel") || ind.contains("cabaña") || ind.contains("posada") ->
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80"
        else ->
            "https://images.unsplash.com/photo-1506377247377-2a5b3b417ebb?auto=format&fit=crop&w=1200&q=80"
    }
}
