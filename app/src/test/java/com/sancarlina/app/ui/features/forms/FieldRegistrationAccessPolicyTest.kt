package com.sancarlina.app.ui.features.forms

import com.sancarlina.app.data.models.FormSchema
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FieldRegistrationAccessPolicyTest {
    private fun form(
        id: String = "commerce-form",
        isPublic: Boolean = true,
        acceptsResponses: Boolean = true,
        fieldRegistrationEnabled: Boolean = false,
        allowedRoles: List<String> = emptyList(),
        assignedUserIds: List<String> = emptyList()
    ) = FormSchema(
        id = id,
        title = "Relevamiento de Comercio",
        isPublic = isPublic,
        acceptsResponses = acceptsResponses,
        fieldRegistrationEnabled = fieldRegistrationEnabled,
        allowedRoles = allowedRoles,
        assignedUserIds = assignedUserIds
    )

    @Test
    fun `form assignment remains valid when profile mirror is stale`() {
        assertTrue(
            FieldRegistrationAccessPolicy.canComplete(
                schema = form(assignedUserIds = listOf("registrar-uid")),
                userId = "registrar-uid",
                role = "registrar",
                profileAssignedFormIds = listOf("another-form")
            )
        )
    }

    @Test
    fun `profile assignment remains valid when form mirror is stale`() {
        assertTrue(
            FieldRegistrationAccessPolicy.canComplete(
                schema = form(assignedUserIds = listOf("another-uid")),
                userId = "registrar-uid",
                role = "registrar",
                profileAssignedFormIds = listOf("commerce-form")
            )
        )
    }

    @Test
    fun `form stays hidden when restrictive sources assign neither user nor form`() {
        assertFalse(
            FieldRegistrationAccessPolicy.canComplete(
                schema = form(assignedUserIds = listOf("another-uid")),
                userId = "registrar-uid",
                role = "registrar",
                profileAssignedFormIds = listOf("another-form")
            )
        )
    }

    @Test
    fun `profile allowlist restricts forms without direct user assignments`() {
        assertFalse(
            FieldRegistrationAccessPolicy.canComplete(
                schema = form(id = "unassigned-form"),
                userId = "registrar-uid",
                role = "registrar",
                profileAssignedFormIds = listOf("commerce-form")
            )
        )
    }

    @Test
    fun `field registration flag permits a non public staff form`() {
        assertTrue(
            FieldRegistrationAccessPolicy.canComplete(
                schema = form(isPublic = false, fieldRegistrationEnabled = true),
                userId = "registrar-uid",
                role = "registrar",
                profileAssignedFormIds = emptyList()
            )
        )
    }

    @Test
    fun `registrar role aliases are compatible and citizen is rejected`() {
        val restrictedForm = form(allowedRoles = listOf("registrar"))

        assertTrue(
            FieldRegistrationAccessPolicy.canComplete(
                restrictedForm,
                "registrar-uid",
                "registrador",
                emptyList()
            )
        )
        assertFalse(
            FieldRegistrationAccessPolicy.canComplete(
                restrictedForm,
                "citizen-uid",
                "citizen",
                emptyList()
            )
        )
    }

    @Test
    fun `fresh staff profile role wins over stale citizen claim`() {
        assertEquals(
            "registrar",
            FieldRegistrationAccessPolicy.resolveRole(claimRole = "citizen", profileRole = "registrar")
        )
    }

    @Test
    fun `closed forms never appear even when assigned`() {
        assertFalse(
            FieldRegistrationAccessPolicy.canComplete(
                schema = form(acceptsResponses = false, assignedUserIds = listOf("registrar-uid")),
                userId = "registrar-uid",
                role = "registrar",
                profileAssignedFormIds = listOf("commerce-form")
            )
        )
    }
}
