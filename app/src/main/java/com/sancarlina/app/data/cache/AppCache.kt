package com.sancarlina.app.data.cache

import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.data.repository.Area
import com.sancarlina.app.data.repository.Benefit
import java.util.concurrent.atomic.AtomicReference

object AppCache {
    private val tenantsCache = AtomicReference<List<Tenant>?>(null)
    private val areasCache = AtomicReference<List<Area>?>(null)
    private val benefitsCache = AtomicReference<List<Benefit>?>(null)
    private var areasLastFetch = 0L
    private var benefitsLastFetch = 0L
    private const val CACHE_DURATION = 15 * 60 * 1000L // 15 minutes

    fun getTenants(): List<Tenant>? = tenantsCache.get()

    fun putTenants(tenants: List<Tenant>) {
        tenantsCache.set(tenants)
    }
    
    fun isAreasCacheValid(): Boolean {
        return areasCache.get() != null && (System.currentTimeMillis() - areasLastFetch) < CACHE_DURATION
    }
    
    fun getAreas(): List<Area>? = areasCache.get()
    
    fun setAreas(areas: List<Area>) {
        areasCache.set(areas)
        areasLastFetch = System.currentTimeMillis()
    }
    
    fun isBenefitsCacheValid(): Boolean {
        return benefitsCache.get() != null && (System.currentTimeMillis() - benefitsLastFetch) < CACHE_DURATION
    }
    
    fun getBenefits(): List<Benefit>? = benefitsCache.get()
    
    fun setBenefits(benefits: List<Benefit>) {
        benefitsCache.set(benefits)
        benefitsLastFetch = System.currentTimeMillis()
    }

    fun clear() {
        tenantsCache.set(null)
        areasCache.set(null)
        benefitsCache.set(null)
    }
}
