package com.sancarlina.app.utils

import android.util.Patterns

object InputValidator {

    /**
     * Valida si un correo electrónico cumple con el formato estándar.
     */
    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Remueve etiquetas HTML/XML simples, caracteres de control y recorta el exceso de longitud
     * para mitigar inyecciones de etiquetas y abuso de cuota de almacenamiento.
     */
    fun sanitizeText(input: String?, maxLength: Int): String {
        if (input.isNullOrBlank()) return ""
        
        // Remover cualquier etiqueta <HTML>
        val noHtml = input.replace(Regex("<[^>]*>"), "")
        
        // Limitar la longitud máxima del texto
        val truncated = if (noHtml.length > maxLength) {
            noHtml.substring(0, maxLength)
        } else {
            noHtml
        }
        
        return truncated.trim()
    }
}
