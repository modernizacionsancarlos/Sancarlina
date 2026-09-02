package com.sancarlina.app.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ItineraryMathTest {
    @Test
    fun `calculates known distance between Paris and London`() {
        val distance = ItineraryMath.haversineKm(48.8566, 2.3522, 51.5074, -0.1278)
        assertEquals(343.6, distance, 1.0)
    }

    @Test
    fun `accepts valid coordinates on Greenwich meridian`() {
        val distance = ItineraryMath.haversineKm(51.4779, 0.0, 48.8566, 2.3522)
        assertTrue(distance > 300.0)
    }

    @Test
    fun `estimates travel plus one hour at each stop`() {
        assertEquals(220, ItineraryMath.estimatedMinutes(distanceKm = 30.0, stops = 3))
    }

    @Test
    fun `ignores missing zero pair coordinates`() {
        assertEquals(0.0, ItineraryMath.haversineKm(0.0, 0.0, -33.7, -69.1), 0.0)
    }
}
