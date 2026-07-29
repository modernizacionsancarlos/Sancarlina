package com.sancarlina.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.models.SuperAdmin
import com.sancarlina.app.data.remote.AdminFunctionsService
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.tasks.await

class AdminAdministradoresRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val functionsService: AdminFunctionsService = AdminFunctionsService()
) {

    suspend fun getAllSuperAdmins(): Result<List<SuperAdmin>> {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.SUPER_ADMINS)
                .get()
                .await()

            val list = snapshot.documents.mapNotNull { doc ->
                try {
                    SuperAdmin(
                        uid = doc.id,
                        email = doc.getString("email") ?: "",
                        active = doc.getBoolean("active") ?: true,
                        role = doc.getString("role") ?: "admin",
                        createdAt = doc.getTimestamp("created_at")
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSuperAdminUser(email: String, password: String, userName: String): Result<Map<String, Any>> {
        return functionsService.createUser(
            email = email,
            password = password,
            userName = userName,
            role = "admin",
            makeSuperAdmin = true
        )
    }

    suspend fun toggleSuperAdminActive(uid: String, email: String, active: Boolean): Result<Map<String, Any>> {
        return functionsService.setSuperAdmin(uid, email, active)
    }
}
