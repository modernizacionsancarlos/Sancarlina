package com.sancarlina.app.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class SuperAdmin(
    val uid: String = "",
    val email: String = "",
    val active: Boolean = true,
    val role: String = "admin",
    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Timestamp? = null
)

data class DashboardStats(
    val totalTenants: Int = 0,
    val activeTenants: Int = 0,
    val pendingSubmissions: Int = 0,
    val activeForms: Int = 0
)

data class AuditLog(
    val id: String = "",
    val action: String = "",
    @get:PropertyName("user_id") @set:PropertyName("user_id")
    var userId: String = "",
    @get:PropertyName("user_email") @set:PropertyName("user_email")
    var userEmail: String = "",
    val timestamp: Timestamp? = null,
    val details: Map<String, Any> = emptyMap()
)
