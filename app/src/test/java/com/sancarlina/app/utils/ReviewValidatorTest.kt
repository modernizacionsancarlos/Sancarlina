package com.sancarlina.app.utils

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ReviewValidatorTest {
    @Test fun `accepts valid review`() {
        assertNull(ReviewValidator.error("tenant-1", 5, "Excelente atención"))
    }

    @Test fun `rejects rating outside range`() {
        assertNotNull(ReviewValidator.error("tenant-1", 0, ""))
    }

    @Test fun `rejects oversized comment after trimming`() {
        assertNotNull(ReviewValidator.error("tenant-1", 4, "a".repeat(1001)))
    }
}
