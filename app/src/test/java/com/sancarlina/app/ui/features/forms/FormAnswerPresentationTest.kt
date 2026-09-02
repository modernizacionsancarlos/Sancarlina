package com.sancarlina.app.ui.features.forms

import com.sancarlina.app.data.models.FormField
import com.sancarlina.app.data.models.FormSchema
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FormAnswerPresentationTest {
    @Test
    fun internalKeysAreHiddenAndFieldLabelsAreUsed() {
        val schema = FormSchema(
            id = "commerce",
            title = "Relevamiento",
            fields = listOf(
                FormField(id = "field_123", label = "Nombre comercial"),
                FormField(id = "enabled", label = "Está abierto")
            )
        )
        val answers = FormAnswerPresentation.answers(
            schema,
            mapOf(
                "field_123" to "Almacén Norte",
                "enabled" to true,
                "created_by" to "internal-user",
                "client_submission_id" to "internal-id"
            )
        )

        assertEquals(listOf("Nombre comercial", "Está abierto"), answers.map { it.label })
        assertEquals(listOf("Almacén Norte", "Sí"), answers.map { it.value })
        assertFalse(answers.any { it.value.contains("internal") })
    }

    @Test
    fun unknownLegacyKeysGetHumanFriendlyNames() {
        val answers = FormAnswerPresentation.answers(null, mapOf("field_legacy_999" to "Dato"))
        assertEquals("Dato adicional 1", answers.single().label)
        assertEquals("Dato", answers.single().value)
    }

    @Test
    fun implicitAccountAuthorizationIsNotShownInReview() {
        val authorization = FormField(
            id = "authorization",
            type = "boolean",
            label = "Autorizo al Municipio a difundir fotos en la App Oficial",
            required = true
        )
        val schema = FormSchema(id = "commerce", fields = listOf(authorization))

        assertTrue(FormAnswerPresentation.answers(schema, mapOf("authorization" to true)).isEmpty())
    }
}
