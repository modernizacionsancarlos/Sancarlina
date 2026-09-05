package com.sancarlina.app.data.cache

import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.data.repository.Area
import com.sancarlina.app.data.repository.Benefit
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

/**
 * Caché en memoria de alto rendimiento para evitar lecturas continuas a Firestore,
 * ahorrar cuota/recursos y hacer que la app cargue de forma instantánea.
 */
object AppCache {
    private val tenantsRef = AtomicReference<List<Tenant>?>(null)
    private val tenantsMap = ConcurrentHashMap<String, Tenant>()

    private val areasRef = AtomicReference<List<Area>?>(null)
    private val benefitsRef = AtomicReference<List<Benefit>?>(null)

    @Volatile
    private var lastTenantsUpdate: Long = 0L

    @Volatile
    private var lastAreasUpdate: Long = 0L

    @Volatile
    private var lastBenefitsUpdate: Long = 0L

    // Tiempo de validez del caché en memoria (30 minutos)
    const val CACHE_TTL_MS = 30 * 60 * 1000L

    fun getTenants(): List<Tenant>? = tenantsRef.get()

    fun getTenant(tenantId: String): Tenant? = tenantsMap[tenantId]

    fun setTenants(tenants: List<Tenant>) {
        tenantsRef.set(tenants)
        tenantsMap.clear()
        tenants.forEach { tenantsMap[it.id] = it }
        lastTenantsUpdate = System.currentTimeMillis()
    }

    fun isTenantsCacheValid(): Boolean {
        val cached = tenantsRef.get()
        return !cached.isNullOrEmpty() && (System.currentTimeMillis() - lastTenantsUpdate < CACHE_TTL_MS)
    }

    fun getAreas(): List<Area>? = areasRef.get()

    fun setAreas(areas: List<Area>) {
        areasRef.set(areas)
        lastAreasUpdate = System.currentTimeMillis()
    }

    fun isAreasCacheValid(): Boolean {
        val cached = areasRef.get()
        return !cached.isNullOrEmpty() && (System.currentTimeMillis() - lastAreasUpdate < CACHE_TTL_MS)
    }

    fun getBenefits(): List<Benefit>? = benefitsRef.get()

    fun setBenefits(benefits: List<Benefit>) {
        benefitsRef.set(benefits)
        lastBenefitsUpdate = System.currentTimeMillis()
    }

    fun isBenefitsCacheValid(): Boolean {
        val cached = benefitsRef.get()
        return !cached.isNullOrEmpty() && (System.currentTimeMillis() - lastBenefitsUpdate < CACHE_TTL_MS)
    }

    fun clear() {
        tenantsRef.set(null)
        tenantsMap.clear()
        areasRef.set(null)
        benefitsRef.set(null)
    }
}
