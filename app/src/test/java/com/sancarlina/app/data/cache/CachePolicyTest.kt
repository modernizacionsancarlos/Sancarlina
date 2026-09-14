package com.sancarlina.app.data.cache

import com.sancarlina.app.data.repository.CatalogVersions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CachePolicyTest {
    @Test
    fun `catalogos estables vencen despues que contenido dinamico`() {
        assertTrue(CacheDataset.AREAS.ttlMillis > CacheDataset.TENANTS.ttlMillis)
        assertTrue(CacheDataset.TENANTS.ttlMillis > CacheDataset.BENEFITS.ttlMillis)
    }

    @Test
    fun `cada dataset usa su version correspondiente`() {
        val versions = CatalogVersions(
            tenants = 11,
            areas = 12,
            benefits = 13,
            forms = 14,
            notifications = 15
        )

        assertEquals(11L, versions.versionFor(CacheDataset.TENANTS))
        assertEquals(12L, versions.versionFor(CacheDataset.AREAS))
        assertEquals(13L, versions.versionFor(CacheDataset.BENEFITS))
        assertEquals(14L, versions.versionFor(CacheDataset.FORMS))
        assertEquals(15L, versions.versionFor(CacheDataset.NOTIFICATIONS))
    }
}
