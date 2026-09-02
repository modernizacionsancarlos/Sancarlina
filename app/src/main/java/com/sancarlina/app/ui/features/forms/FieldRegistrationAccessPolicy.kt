package com.sancarlina.app.ui.features.forms

import com.sancarlina.app.data.models.FormSchema
import java.util.Locale

internal object FieldRegistrationAccessPolicy {
    private val fieldStaffRoles = setOf("admin", "registrar", "registrador", "field_registrar", "staff")
    private val registrarAliases = setOf("registrar", "registrador", "field_registrar", "staff")

    fun isFieldStaffRole(role: String): Boolean = normalizedRole(role) in fieldStaffRoles

    fun resolveRole(claimRole: String?, profileRole: String): String {
        val normalizedProfileRole = profileRole.trim()
        val normalizedClaimRole = claimRole?.trim().orEmpty()
        return when {
            isFieldStaffRole(normalizedProfileRole) -> normalizedProfileRole
            isFieldStaffRole(normalizedClaimRole) -> normalizedClaimRole
            normalizedClaimRole.isNotEmpty() -> normalizedClaimRole
            else -> normalizedProfileRole.ifEmpty { "citizen" }
        }
    }

    fun canComplete(
        schema: FormSchema,
        userId: String,
        role: String,
        profileAssignedFormIds: List<String>
    ): Boolean {
        if (!schema.acceptsResponses) return false
        if (!schema.isPublic && !schema.fieldRegistrationEnabled) return false
        if (!isRoleAllowed(role, schema.allowedRoles)) return false

        val normalizedUserId = userId.trim()
        val formAssignments = schema.assignedUserIds
            .map(String::trim)
            .filter(String::isNotEmpty)
        val profileAssignments = profileAssignedFormIds
            .map(String::trim)
            .filter(String::isNotEmpty)

        val assignedFromForm = normalizedUserId.isNotEmpty() && normalizedUserId in formAssignments
        val assignedFromProfile = schema.id.trim() in profileAssignments

        return when {
            formAssignments.isNotEmpty() && profileAssignments.isNotEmpty() ->
                assignedFromForm || assignedFromProfile
            formAssignments.isNotEmpty() -> assignedFromForm
            profileAssignments.isNotEmpty() -> assignedFromProfile
            else -> true
        }
    }

    private fun isRoleAllowed(role: String, allowedRoles: List<String>): Boolean {
        if (allowedRoles.isEmpty()) return true
        val normalizedRole = normalizedRole(role)
        if (normalizedRole == "admin") return true
        return allowedRoles.any { allowed ->
            val normalizedAllowed = normalizedRole(allowed)
            normalizedAllowed == normalizedRole ||
                (normalizedRole in registrarAliases && normalizedAllowed in registrarAliases)
        }
    }

    private fun normalizedRole(role: String): String = role.trim().lowercase(Locale.ROOT)
}
