package com.example.sancarlina.data.repository

import android.util.Log
import com.example.sancarlina.data.model.Tenant
import com.example.sancarlina.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TenantsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private var cachedTenants: List<Tenant>? = null

    suspend fun getActiveTenants(): List<Tenant> {
        cachedTenants?.let { return it }
        
        return try {
            val snapshot = firestore.collection(FirestoreCollections.TENANTS)
                .whereEqualTo("status", "active")
                .get()
                .await()
            
            val tenants = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Tenant::class.java)?.copy(id = doc.id)
            }
            cachedTenants = tenants
            
            Log.d("TenantsRepository", "Fetched ${tenants.size} active tenants from Firestore")
            tenants
        } catch (e: Exception) {
            Log.e("TenantsRepository", "Error fetching tenants", e)
            emptyList()
        }
    }
    
    fun clearCache() {
        cachedTenants = null
    }
}
