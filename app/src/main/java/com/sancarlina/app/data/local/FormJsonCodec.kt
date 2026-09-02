package com.sancarlina.app.data.local

import com.sancarlina.app.data.models.FormField
import com.sancarlina.app.data.models.FormSchema
import org.json.JSONArray
import org.json.JSONObject

internal object FormJsonCodec {
    fun encodeSchema(schema: FormSchema): String = JSONObject().apply {
        put("id", schema.id)
        put("title", schema.title)
        put("description", schema.description)
        put("tenantId", schema.tenantId)
        put("tenant_id", schema.tenant_id)
        put("submit_url", schema.submitUrl)
        put("is_public", schema.isPublic)
        put("accepts_responses", schema.acceptsResponses)
        put("status", schema.status)
        put("form_purpose", schema.formPurpose)
        put("template_source", schema.templateSource)
        put("template_category", schema.templateCategory)
        put("municipality_notes", schema.municipalityNotes)
        put("allowed_roles", JSONArray(schema.allowedRoles))
        put("assigned_user_ids", JSONArray(schema.assignedUserIds))
        put("field_registration_enabled", schema.fieldRegistrationEnabled)
        put("fields", JSONArray().apply {
            schema.fields.forEach { field ->
                put(JSONObject().apply {
                    put("id", field.id)
                    put("type", field.type)
                    put("label", field.label)
                    put("required", field.required)
                    put("tenant_mapping", field.tenantMapping)
                    put("options", JSONArray(field.options))
                    put("placeholder", field.placeholder)
                    put("help_text", field.helpText)
                    put("description", field.description)
                    put("max_images", field.maxImages)
                    put("allow_multiple", field.allowMultiple)
                })
            }
        })
    }.toString()

    fun decodeSchema(json: String): FormSchema {
        val root = JSONObject(json)
        val fieldsJson = root.optJSONArray("fields") ?: JSONArray()
        val fields = buildList {
            for (index in 0 until fieldsJson.length()) {
                val field = fieldsJson.getJSONObject(index)
                val optionsJson = field.optJSONArray("options") ?: JSONArray()
                val options = buildList {
                    for (optionIndex in 0 until optionsJson.length()) {
                        add(optionsJson.optString(optionIndex))
                    }
                }
                add(
                    FormField(
                        id = field.optString("id"),
                        type = field.optString("type", "text"),
                        label = field.optString("label"),
                        required = field.optBoolean("required"),
                        tenantMapping = field.optNullableString("tenant_mapping"),
                        options = options,
                        placeholder = field.optNullableString("placeholder"),
                        helpText = field.optNullableString("help_text"),
                        description = field.optNullableString("description"),
                        maxImages = field.optInt("max_images", 1).coerceAtLeast(1),
                        allowMultiple = field.optBoolean("allow_multiple")
                    )
                )
            }
        }
        return FormSchema(
            id = root.optString("id"),
            title = root.optString("title"),
            description = root.optString("description"),
            tenantId = root.optString("tenantId"),
            tenant_id = root.optString("tenant_id"),
            submitUrl = root.optNullableString("submit_url"),
            isPublic = root.optBoolean("is_public"),
            acceptsResponses = root.optBoolean("accepts_responses"),
            status = root.optString("status", "draft"),
            fields = fields,
            formPurpose = root.optNullableString("form_purpose"),
            templateSource = root.optNullableString("template_source"),
            templateCategory = root.optNullableString("template_category"),
            municipalityNotes = root.optNullableString("municipality_notes"),
            allowedRoles = root.optStringList("allowed_roles"),
            assignedUserIds = root.optStringList("assigned_user_ids"),
            fieldRegistrationEnabled = root.optBoolean("field_registration_enabled")
        )
    }

    fun encodeValues(values: Map<String, Any?>): String = JSONObject().apply {
        values.forEach { (key, value) -> put(key, toJsonValue(value)) }
    }.toString()

    fun decodeValues(json: String): Map<String, Any?> = jsonObjectToMap(JSONObject(json))

    private fun toJsonValue(value: Any?): Any = when (value) {
        null -> JSONObject.NULL
        is Map<*, *> -> JSONObject().apply {
            value.forEach { (key, nested) -> if (key != null) put(key.toString(), toJsonValue(nested)) }
        }
        is Iterable<*> -> JSONArray().apply { value.forEach { put(toJsonValue(it)) } }
        is Array<*> -> JSONArray().apply { value.forEach { put(toJsonValue(it)) } }
        is Boolean, is Number, is String -> value
        else -> value.toString()
    }

    private fun jsonObjectToMap(source: JSONObject): Map<String, Any?> = buildMap {
        val keys = source.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            put(key, fromJsonValue(source.get(key)))
        }
    }

    private fun fromJsonValue(value: Any?): Any? = when (value) {
        null, JSONObject.NULL -> null
        is JSONObject -> jsonObjectToMap(value)
        is JSONArray -> buildList {
            for (index in 0 until value.length()) add(fromJsonValue(value.get(index)))
        }
        else -> value
    }

    private fun JSONObject.optNullableString(key: String): String? =
        if (!has(key) || isNull(key)) null else optString(key).takeIf { it.isNotBlank() }

    private fun JSONObject.optStringList(key: String): List<String> {
        val source = optJSONArray(key) ?: return emptyList()
        return buildList {
            for (index in 0 until source.length()) {
                source.optString(index).trim().takeIf(String::isNotBlank)?.let(::add)
            }
        }
    }
}
