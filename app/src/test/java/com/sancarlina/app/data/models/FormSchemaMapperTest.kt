package com.sancarlina.app.data.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FormSchemaMapperTest {
    @Test
    fun `maps camel and snake case form metadata without losing fields`() {
        val schema = FormSchema.fromMap(
            id = "form-1",
            data = mapOf(
                "title" to "Inscripción",
                "tenant_id" to "tenant-1",
                "submitUrl" to "https://example.com/submit",
                "is_public" to true,
                "accepts_responses" to true,
                "field_registration_enabled" to true,
                "allowed_roles" to listOf("registrar"),
                "assigned_user_ids" to listOf("uid-1", "uid-2"),
                "formPurpose" to "registro",
                "fields" to listOf(
                    mapOf(
                        "id" to "photos",
                        "type" to "image",
                        "label" to "Fotos",
                        "tenantMapping" to "gallery",
                        "helpText" to "Subí imágenes",
                        "maxImages" to 4L,
                        "allowMultiple" to true
                    )
                )
            )
        )

        assertEquals("tenant-1", schema.tenantId)
        assertEquals("https://example.com/submit", schema.submitUrl)
        assertEquals("registro", schema.formPurpose)
        assertTrue(schema.isPublic)
        assertTrue(schema.acceptsResponses)
        assertTrue(schema.fieldRegistrationEnabled)
        assertEquals(listOf("registrar"), schema.allowedRoles)
        assertEquals(listOf("uid-1", "uid-2"), schema.assignedUserIds)
        assertEquals("gallery", schema.fields.single().tenantMapping)
        assertEquals(4, schema.fields.single().maxImages)
        assertTrue(schema.fields.single().allowMultiple)
    }
}
