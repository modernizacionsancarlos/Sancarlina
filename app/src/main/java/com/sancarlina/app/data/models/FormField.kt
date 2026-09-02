package com.sancarlina.app.data.models

import com.google.firebase.firestore.PropertyName

data class FormField(
    val id: String = "",
    val type: String = "text",
    val label: String = "",
    val required: Boolean = false,
    @get:PropertyName("tenant_mapping") @set:PropertyName("tenant_mapping")
    var tenantMapping: String? = null,
    val options: List<String> = emptyList(),
    val placeholder: String? = null,
    @get:PropertyName("help_text") @set:PropertyName("help_text")
    var helpText: String? = null,
    val description: String? = null,
    @get:PropertyName("max_images") @set:PropertyName("max_images")
    var maxImages: Int = 1,
    @get:PropertyName("allow_multiple") @set:PropertyName("allow_multiple")
    var allowMultiple: Boolean = false
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): FormField {
            fun value(vararg keys: String): Any? = keys.firstNotNullOfOrNull { data[it] }
            fun text(vararg keys: String): String? = value(*keys)?.toString()?.trim()
            fun bool(vararg keys: String): Boolean = when (val raw = value(*keys)) {
                is Boolean -> raw
                is Number -> raw.toInt() != 0
                is String -> raw.equals("true", ignoreCase = true) || raw == "1"
                else -> false
            }
            val options = (value("options", "choices") as? List<*>)
                ?.mapNotNull { it?.toString() }
                .orEmpty()
            val maxImages = when (val raw = value("maxImages", "max_images")) {
                is Number -> raw.toInt()
                is String -> raw.toIntOrNull() ?: 1
                else -> 1
            }
            return FormField(
                id = text("id", "key", "name").orEmpty(),
                type = text("type", "fieldType", "field_type").orEmpty().ifBlank { "text" },
                label = text("label", "title").orEmpty(),
                required = bool("required", "isRequired", "is_required"),
                tenantMapping = text("tenantMapping", "tenant_mapping"),
                options = options,
                placeholder = text("placeholder"),
                helpText = text("helpText", "help_text"),
                description = text("description"),
                maxImages = maxImages,
                allowMultiple = bool("allowMultiple", "allow_multiple")
            )
        }
    }
}
