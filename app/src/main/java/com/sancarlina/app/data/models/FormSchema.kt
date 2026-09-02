package com.sancarlina.app.data.models

import com.google.firebase.firestore.PropertyName

data class FormSchema(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    @get:PropertyName("tenantId") @set:PropertyName("tenantId")
    var tenantId: String = "",
    @get:PropertyName("tenant_id") @set:PropertyName("tenant_id")
    var tenant_id: String = "",
    @get:PropertyName("submit_url") @set:PropertyName("submit_url")
    var submitUrl: String? = null,
    @get:PropertyName("is_public") @set:PropertyName("is_public")
    var isPublic: Boolean = false,
    @get:PropertyName("accepts_responses") @set:PropertyName("accepts_responses")
    var acceptsResponses: Boolean = false,
    val status: String = "draft",
    val fields: List<FormField> = emptyList(),
    @get:PropertyName("form_purpose") @set:PropertyName("form_purpose")
    var formPurpose: String? = null,
    @get:PropertyName("template_source") @set:PropertyName("template_source")
    var templateSource: String? = null,
    @get:PropertyName("template_category") @set:PropertyName("template_category")
    var templateCategory: String? = null,
    @get:PropertyName("municipality_notes") @set:PropertyName("municipality_notes")
    var municipalityNotes: String? = null,
    @get:PropertyName("allowed_roles") @set:PropertyName("allowed_roles")
    var allowedRoles: List<String> = emptyList(),
    @get:PropertyName("assigned_user_ids") @set:PropertyName("assigned_user_ids")
    var assignedUserIds: List<String> = emptyList(),
    @get:PropertyName("field_registration_enabled") @set:PropertyName("field_registration_enabled")
    var fieldRegistrationEnabled: Boolean = false
) {
    companion object {
        fun fromMap(id: String, data: Map<String, Any?>): FormSchema {
            fun value(vararg keys: String): Any? = keys.firstNotNullOfOrNull { data[it] }
            fun text(vararg keys: String): String? = value(*keys)?.toString()?.trim()
            fun bool(vararg keys: String): Boolean = when (val raw = value(*keys)) {
                is Boolean -> raw
                is Number -> raw.toInt() != 0
                is String -> raw.equals("true", ignoreCase = true) || raw == "1"
                else -> false
            }
            val fields = (value("fields") as? List<*>)?.mapNotNull { raw ->
                when (raw) {
                    is FormField -> raw
                    is Map<*, *> -> {
                        @Suppress("UNCHECKED_CAST")
                        FormField.fromMap(raw as Map<String, Any?>)
                    }
                    else -> null
                }
            }.orEmpty()
            val sharedTenantId = text("tenantId", "tenant_id").orEmpty()
            return FormSchema(
                id = id,
                title = text("title", "name").orEmpty(),
                description = text("description").orEmpty(),
                tenantId = sharedTenantId,
                tenant_id = sharedTenantId,
                submitUrl = text("submitUrl", "submit_url"),
                isPublic = bool("is_public", "isPublic"),
                acceptsResponses = bool("accepts_responses", "acceptsResponses"),
                status = text("status").orEmpty().ifBlank { "draft" },
                fields = fields,
                formPurpose = text("formPurpose", "form_purpose"),
                templateSource = text("templateSource", "template_source"),
                templateCategory = text("templateCategory", "template_category"),
                municipalityNotes = text("municipalityNotes", "municipality_notes"),
                allowedRoles = (value("allowedRoles", "allowed_roles") as? List<*>)
                    ?.mapNotNull { it?.toString()?.trim()?.takeIf(String::isNotBlank) }
                    .orEmpty(),
                assignedUserIds = (value("assignedUserIds", "assigned_user_ids") as? List<*>)
                    ?.mapNotNull { it?.toString()?.trim()?.takeIf(String::isNotBlank) }
                    .orEmpty(),
                fieldRegistrationEnabled = bool("fieldRegistrationEnabled", "field_registration_enabled")
            )
        }
    }
}
