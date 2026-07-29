package com.sancarlina.app.data.remote

import com.google.firebase.functions.FirebaseFunctions
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.tasks.await

class AdminFunctionsService(
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance(FirestoreCollections.FUNCTIONS_REGION)
) {

    suspend fun createUser(
        email: String,
        password: String,
        userName: String,
        role: String = "citizen",
        makeSuperAdmin: Boolean = false
    ): Result<Map<String, Any>> {
        return try {
            val data = mapOf(
                "email" to email,
                "password" to password,
                "userName" to userName,
                "role" to role,
                "makeSuperAdmin" to makeSuperAdmin
            )
            val result = functions
                .getHttpsCallable("adminCreateUser")
                .call(data)
                .await()

            @Suppress("UNCHECKED_CAST")
            val resMap = result.data as? Map<String, Any> ?: emptyMap()
            Result.success(resMap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setSuperAdmin(
        uid: String,
        email: String,
        active: Boolean
    ): Result<Map<String, Any>> {
        return try {
            val data = mapOf(
                "uid" to uid,
                "email" to email,
                "active" to active
            )
            val result = functions
                .getHttpsCallable("adminSetSuperAdmin")
                .call(data)
                .await()

            @Suppress("UNCHECKED_CAST")
            val resMap = result.data as? Map<String, Any> ?: emptyMap()
            Result.success(resMap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(
        uid: String,
        password: String
    ): Result<Map<String, Any>> {
        return try {
            val data = mapOf(
                "uid" to uid,
                "password" to password
            )
            val result = functions
                .getHttpsCallable("adminResetPassword")
                .call(data)
                .await()

            @Suppress("UNCHECKED_CAST")
            val resMap = result.data as? Map<String, Any> ?: emptyMap()
            Result.success(resMap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
