package com.sancarlina.app.ui.features.points

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QrScannerViewModelTest {

    @Test
    fun `parses a valid points QR`() {
        assertEquals(
            PointsQrPayload(tenantId = "commerce-42", points = 25),
            parsePointsQr("sancarlina:points:commerce-42:25")
        )
    }

    @Test
    fun `rejects malformed or unsafe values`() {
        listOf(
            "",
            "sancarlina:points:commerce-42",
            "sancarlina:points::25",
            "sancarlina:points:commerce-42:0",
            "sancarlina:points:commerce-42:-1",
            "sancarlina:points:commerce-42:not-a-number",
            "other:points:commerce-42:25",
            "sancarlina:other:commerce-42:25"
        ).forEach { value ->
            assertNull(value, parsePointsQr(value))
        }
    }
}
