package com.sancarlina.app.data.repository

import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.utils.Logger
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
            
            Logger.d("Fetched ${tenants.size} active tenants from Firestore")
            tenants
        } catch (e: Exception) {
            Logger.e("Error fetching tenants", e)
            emptyList()
        }
    }
    
    fun clearCache() {
        cachedTenants = null
    }
}
