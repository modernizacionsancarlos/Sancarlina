package com.sancarlina.app.ui.features.forms

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AttachmentPickerPolicyTest {

    @Test
    fun `single attachment keeps one selection but builds a valid multiple contract`() {
        val policy = attachmentPickerPolicy(maxItems = 1)

        assertEquals(1, policy.selectionLimit)
        assertFalse(policy.multiple)
    }

    @Test
    fun `invalid external limits are normalized before rendering`() {
        val policy = attachmentPickerPolicy(maxItems = 0)

        assertEquals(1, policy.selectionLimit)
    }

    @Test
    fun `multiple attachment limit is preserved`() {
        val policy = attachmentPickerPolicy(maxItems = 5)

        assertEquals(5, policy.selectionLimit)
        assertTrue(policy.multiple)
    }
}
