package com.sancarlina.app.data.repository

import com.google.firebase.functions.FirebaseFunctions
import com.sancarlina.app.analytics.AppAnalytics
import kotlinx.coroutines.tasks.await

class EngagementRepository(
    private val functions: FirebaseFunctions,
    private val analytics: AppAnalytics
) {
    suspend fun trackTenantAction(tenantId: String, tenantName: String, action: String) {
        if (tenantId.isBlank() || action.isBlank()) return
        analytics.logTenantAction(tenantId, tenantName, action)
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
