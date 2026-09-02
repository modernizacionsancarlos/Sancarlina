package com.sancarlina.app.utils

object ReviewValidator {
    const val MAX_COMMENT_LENGTH = 1000

    fun error(tenantId: String, rating: Int, comment: String): String? = when {
        tenantId.isBlank() -> "No encontramos el comercio a calificar."
        rating !in 1..5 -> "Elegí una calificación entre 1 y 5 estrellas."
        comment.trim().length > MAX_COMMENT_LENGTH ->
            "La reseña no puede superar los $MAX_COMMENT_LENGTH caracteres."
        else -> null
    }
}
