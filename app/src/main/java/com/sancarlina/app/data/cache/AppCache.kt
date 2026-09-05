package com.sancarlina.app.data.cache

import com.sancarlina.app.data.models.Tenant
import java.util.concurrent.atomic.AtomicReference

object AppCache {
    private val tenantsCache = AtomicReference<List<Tenant>?>(null)

    fun getTenants(): List<Tenant>? = tenantsCache.get()

    fun putTenants(tenants: List<Tenant>) {
        tenantsCache.set(tenants)
    }

    fun clear() {
        tenantsCache.set(null)
    }
}
