package com.sancarlina.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.models.DashboardStats
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.tasks.await

class AdminRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun getDashboardStats(): Result<DashboardStats> {
        return try {
            val tenantsSnap = firestore.collection(FirestoreCollections.TENANTS)
                .get()
                .await()

            val activeTenants = tenantsSnap.documents.count { doc ->
                doc.getString("status") == "active" || doc.getBoolean("active") == true
            }

            val submissionsSnap = firestore.collection(FirestoreCollections.SUBMISSIONS)
                .get()
                .await()

            val pendingSubmissions = submissionsSnap.documents.count { doc ->
                val status = doc.getString("status")?.lowercase()
                status == "pending" || status == "pendiente" || status == "submitted"
            }

            val formsSnap = firestore.collection(FirestoreCollections.FORM_SCHEMAS)
                .get()
                .await()

            val activeForms = formsSnap.documents.count { doc ->
                doc.getString("status") == "active" || doc.getBoolean("is_public") == true
            }

            Result.success(
                DashboardStats(
                    totalTenants = tenantsSnap.size(),
                    activeTenants = activeTenants,
                    pendingSubmissions = pendingSubmissions,
                    activeForms = activeForms
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logAdminAction(action: String, details: Map<String, Any> = emptyMap()): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Sin usuario autenticado"))
        return try {
            val logData = mapOf(
                "action" to action,
                "user_id" to user.uid,
                "user_email" to (user.email ?: ""),
                "timestamp" to Timestamp.now(),
                "details" to details
            )
            firestore.collection(FirestoreCollections.AUDIT_LOGS)
                .add(logData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
