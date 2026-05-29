package com.example.sancarlina.data.repository

import android.util.Log
import com.example.sancarlina.data.model.Tenant
import com.example.sancarlina.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TenantsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getActiveTenants(): List<Tenant> {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.TENANTS)
                .whereEqualTo("status", "active")
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Tenant::class.java)?.copy(id = doc.id)
            }.also {
                Log.d("TenantsRepository", "Fetched ${it.size} active tenants from Firestore")
                it.forEach { tenant ->
                    Log.d("TenantsRepository", "Tenant: ${tenant.name}")
                }
            }
        } catch (e: Exception) {
            Log.e("TenantsRepository", "Error fetching tenants", e)
            emptyList()
        }
    }
}
