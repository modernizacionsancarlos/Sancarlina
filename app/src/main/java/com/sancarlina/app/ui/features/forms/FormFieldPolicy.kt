package com.sancarlina.app.ui.features.forms

import com.sancarlina.app.data.models.FormField
import com.sancarlina.app.data.models.FormSchema
import java.text.Normalizer
import java.util.Locale

/**
 * Reglas de compatibilidad para esquemas creados antes del flujo de registro de campo.
 * La autorización general ya se acepta al usar una cuenta municipal y no debe bloquear
 * cada relevamiento. Aun así se envía `true` para que los validadores de esquemas antiguos
 * continúen funcionando.
 */
internal fun FormField.isImplicitAccountAuthorization(): Boolean {
    val normalizedType = type.normalizedForPolicy()
    val text = listOf(id, label, tenantMapping, helpText, description)
        .filterNotNull()
        .joinToString(" ")
        .normalizedForPolicy()

    val isBooleanLike = normalizedType in setOf(
        "boolean",
        "checkbox",
        "switch",
        "consent",
        "authorization",
        "autorizacion"
    )
    val mentionsAuthorization = text.contains("autoriz") || text.contains("consent")
    val describesGeneralMunicipalUse = listOf(
        "municipio",
        "municipalidad",
        "difundir",
        "publicar",
        "app oficial",
        "terminos",
        "condiciones"
    ).any(text::contains)

    return mentionsAuthorization && (isBooleanLike || describesGeneralMunicipalUse)
}

internal fun visibleRegistrationFields(fields: List<FormField>): List<FormField> =
    fields.filterIndexed { index, field ->
        when {
            field.isImplicitAccountAuthorization() -> false
            field.type.equals("section", ignoreCase = true) &&
                field.label.normalizedForPolicy().contains("autoriz") -> {
                fields.drop(index + 1)
                    .firstOrNull { !it.type.equals("section", ignoreCase = true) }
                    ?.isImplicitAccountAuthorization() != true
            }
            else -> true
        }
    }

internal fun valuesWithImplicitAccountAuthorization(
    schema: FormSchema,
    values: Map<String, Any?>
): Map<String, Any?> {
    val implicitFields = schema.fields.filter(FormField::isImplicitAccountAuthorization)
    if (implicitFields.isEmpty()) return values
    return values.toMutableMap().apply {
        implicitFields.forEach { field -> put(field.id, true) }
    }
}

internal fun FormField.isAddressField(): Boolean {
    val mapping = tenantMapping.orEmpty().normalizedForPolicy()
    if (mapping in setOf("address", "full_address", "street_address", "direccion", "direccion_completa")) {
        return true
    }
    val text = listOf(label, placeholder, helpText)
        .filterNotNull()
        .joinToString(" ")
        .normalizedForPolicy()
    return text.contains("direccion completa") || text.contains("domicilio")
}

internal fun buildMunicipalAddressQuery(address: String, district: String?): String {
    val parts = listOf(
        address.trim(),
        district?.trim().orEmpty(),
        "San Carlos",
        "Mendoza",
        "Argentina"
    ).filter(String::isNotBlank)

    return parts.fold(mutableListOf<String>()) { unique, part ->
        if (unique.none { it.equals(part, ignoreCase = true) }) unique += part
        unique
    }.joinToString(", ")
}

private fun String.normalizedForPolicy(): String = Normalizer
    .normalize(this, Normalizer.Form.NFD)
    .replace("\\p{M}+".toRegex(), "")
    .lowercase(Locale.ROOT)
    .trim()
