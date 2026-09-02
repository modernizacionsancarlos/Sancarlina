package com.sancarlina.app.ui.features.forms

import com.google.firebase.Timestamp
import com.sancarlina.app.data.models.FormSchema
import java.text.DateFormat

data class PresentedAnswer(
    val fieldId: String,
    val label: String,
    val value: String
)

object FormAnswerPresentation {
    val metadataKeys = setOf(
        "form_id",
        "form_title",
        "created_by",
        "created_at",
        "client_updated_at",
        "client_submission_id",
        "synced_at",
        "status",
        "data"
    )

    fun answers(schema: FormSchema?, data: Map<String, *>): List<PresentedAnswer> {
        val fields = schema?.fields.orEmpty().filterNot {
            it.type == "section" || it.isImplicitAccountAuthorization()
        }
        val known = fields.mapNotNull { field ->
            data[field.id]?.let { value ->
                PresentedAnswer(field.id, field.label.ifBlank { "Dato" }, formatValue(value))
            }
        }
        val knownIds = schema?.fields.orEmpty().mapTo(mutableSetOf()) { it.id }
        val extra = data.entries
            .filter { (key, value) -> key !in metadataKeys && key !in knownIds && value != null }
            .mapIndexed { index, (key, value) ->
                PresentedAnswer(key, "Dato adicional ${index + 1}", formatValue(value))
            }
        return known + extra
    }

    fun formatValue(value: Any?): String = when (value) {
        null -> "Sin completar"
        is Boolean -> if (value) "Sí" else "No"
        is Timestamp -> DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
            .format(value.toDate())
        is Iterable<*> -> {
            val values = value.mapNotNull { it?.let(::formatSingleValue) }
            if (values.isEmpty()) "Sin completar" else values.joinToString(" · ")
        }
        else -> formatSingleValue(value)
    }

    fun statusLabel(status: String): String = when (status.lowercase()) {
        "pending" -> "Pendiente de revisión"
        "approved", "published" -> "Aprobado"
        "rejected" -> "Rechazado"
        else -> "En revisión"
    }

    private fun formatSingleValue(value: Any): String {
        val text = value.toString().trim()
        return when {
            text.isBlank() -> "Sin completar"
            text.startsWith("https://", ignoreCase = true) -> "Archivo adjunto"
            text.equals("true", ignoreCase = true) -> "Sí"
            text.equals("false", ignoreCase = true) -> "No"
            else -> text
        }
    }
}
