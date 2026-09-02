package com.sancarlina.app.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AreaMapperTest {
    @Test
    fun `maps firestore numeric and alias values without reflection`() {
        val area = Area.fromMap(
            id = "area-1",
            data = mapOf(
                "title" to "Centro",
                "position" to 7L,
                "type" to "geographic",
                "icon_name" to "location_city",
                "is_active" to "false"
            )
        )

        assertEquals("area-1", area.id)
        assertEquals("Centro", area.name)
        assertEquals(7, area.order)
        assertEquals("geographic", area.category)
        assertEquals("location_city", area.icon)
        assertFalse(area.active)
    }

    @Test
    fun `uses safe defaults for incomplete documents`() {
        val area = Area.fromMap("area-empty", mapOf("order" to "invalid"))

        assertEquals(0, area.order)
        assertEquals("geographic", area.category)
        assertTrue(area.active)
    }
}
