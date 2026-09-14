package com.sancarlina.app.data.repository

import com.google.firebase.functions.FirebaseFunctions
import com.sancarlina.app.analytics.AppAnalytics
import kotlinx.coroutines.tasks.await
import com.sancarlina.app.utils.RateLimiter

class EngagementRepository(
    private val functions: FirebaseFunctions,
    private val analytics: AppAnalytics
) {
    suspend fun trackTenantAction(tenantId: String, tenantName: String, action: String) {
        if (tenantId.isBlank() || action.isBlank()) return
        analytics.logTenantAction(tenantId, tenantName, action)
        if (!RateLimiter.isActionAllowed("conversion:$tenantId:$action", 30_000L)) return
        functions.getHttpsCallable("trackConversion")
            .call(
                mapOf(
                    "tenantId" to tenantId,
                    "tenantName" to tenantName,
                    "action" to action,
                    "platform" to "android"
                )
            )
            .await()
    }
}
