package com.sancarlina.app.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AppAnalytics(context: Context) {
    private val analytics = FirebaseAnalytics.getInstance(context)

    fun logScreen(route: String) {
        if (route.isBlank()) return
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, route.take(100))
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Compose")
        })
    }

    fun logTenantAction(tenantId: String, tenantName: String, action: String) {
        analytics.logEvent("tenant_action", Bundle().apply {
            putString("tenant_id", tenantId.take(100))
            putString("tenant_name", tenantName.take(100))
            putString("action", action.take(40))
        })
    }
}
