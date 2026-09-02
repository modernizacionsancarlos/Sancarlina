package com.sancarlina.app.ui.features.forms

import com.sancarlina.app.data.models.FormField
import com.sancarlina.app.data.models.FormSchema
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FormFieldPolicyTest {
    private val authorization = FormField(
        id = "field_authorization",
        type = "boolean",
        label = "Autorizo al Municipio a difundir la información y fotos en la App Oficial",
        required = true
    )

    @Test
    fun municipalPublicationAuthorizationIsImplicitForAuthenticatedAccount() {
        assertTrue(authorization.isImplicitAccountAuthorization())

        val schema = FormSchema(id = "commerce", fields = listOf(authorization))
        assertEquals(true, valuesWithImplicitAccountAuthorization(schema, emptyMap())[authorization.id])
        assertEquals(true, valuesWithImplicitAccountAuthorization(schema, mapOf(authorization.id to false))[authorization.id])
    }

    @Test
    fun authorizationSectionAndFieldAreHiddenFromRegistration() {
        val section = FormField(id = "authorization_section", type = "section", label = "Autorización")
        val notes = FormField(id = "notes", type = "textarea", label = "Observaciones")

        assertEquals(listOf(notes), visibleRegistrationFields(listOf(section, authorization, notes)))
    }

    @Test
    fun ordinaryBooleanQuestionRemainsVisibleAndRequired() {
        val ordinaryBoolean = FormField(
            id = "open_today",
            type = "boolean",
            label = "¿El comercio está abierto hoy?",
            required = true
        )

        assertFalse(ordinaryBoolean.isImplicitAccountAuthorization())
        assertEquals(listOf(ordinaryBoolean), visibleRegistrationFields(listOf(ordinaryBoolean)))
    }

    @Test
    fun addressFieldAndMunicipalQueryAreDetected() {
        val address = FormField(
            id = "address",
            type = "text",
            label = "Dirección completa",
            tenantMapping = "address"
        )

        assertTrue(address.isAddressField())
        assertEquals(
            "Bernardo Quiroga 732, Villa San Carlos, San Carlos, Mendoza, Argentina",
            buildMunicipalAddressQuery("Bernardo Quiroga 732", "Villa San Carlos")
        )
    }
}
