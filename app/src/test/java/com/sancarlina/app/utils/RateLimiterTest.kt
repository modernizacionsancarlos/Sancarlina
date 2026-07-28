package com.sancarlina.app.utils

import org.junit.Assert.*
import org.junit.Test

class RateLimiterTest {

    @Test
    fun testWindowAllowed_limitsAttemptsCorrectly() {
        val actionKey = "test_admin_login"
        val maxAttempts = 3
        val windowMillis = 60000L

        // Los primeros 3 intentos deben ser permitidos
        assertTrue(RateLimiter.isWindowAllowed(actionKey, maxAttempts, windowMillis))
        assertTrue(RateLimiter.isWindowAllowed(actionKey, maxAttempts, windowMillis))
        assertTrue(RateLimiter.isWindowAllowed(actionKey, maxAttempts, windowMillis))

        // El 4to intento debe ser bloqueado por exceder el máximo de la ventana
        assertFalse(RateLimiter.isWindowAllowed(actionKey, maxAttempts, windowMillis))
    }
}
