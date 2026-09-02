package com.sancarlina.app.data.models

import org.junit.Assert.assertEquals
import org.junit.Test

class TenantMapperTest {
    @Test
    fun `maps the field aliases used by the web app`() {
        val tenant = Tenant.fromMap(
            id = "tenant-1",
            data = mapOf(
                "name" to "Bodega compartida",
                "category" to "Bodega",
                "short_description" to "Descripción web",
                "phone_number" to "+54 123",
                "locality" to "La Consulta",
                "logo_url" to "https://example.com/logo.jpg",
                "avg_rating" to "4.7",
                "review_count" to 12L,
                "geo_coordinates" to "-33.75,-69.12,950"
            )
        )

        assertEquals("tenant-1", tenant.id)
        assertEquals("Bodega", tenant.industry)
        assertEquals("Descripción web", tenant.description)
        assertEquals("+54 123", tenant.contactPhone)
        assertEquals("La Consulta", tenant.address)
        assertEquals("https://example.com/logo.jpg", tenant.displayImageUrl())
        assertEquals(4.7, tenant.rating, 0.001)
        assertEquals(12, tenant.reviewsCount)
        assertEquals(-33.75, tenant.latitude!!, 0.001)
        assertEquals(-69.12, tenant.longitude!!, 0.001)
    }
}
