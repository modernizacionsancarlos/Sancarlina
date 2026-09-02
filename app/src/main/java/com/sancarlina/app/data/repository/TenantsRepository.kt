package com.sancarlina.app.data.repository

import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
            
            val tenants = snapshot.documents.mapNotNull(::mapTenant)
            
            Logger.d("Fetched ${tenants.size} active tenants from Firestore")
            tenants
        } catch (e: Exception) {
            Logger.e("Error fetching tenants", e)
            emptyList()
        }
    }

    suspend fun getTenantById(tenantId: String): Tenant? {
        if (tenantId.isBlank()) return null
        return try {
            val doc = firestore.collection(FirestoreCollections.TENANTS)
                .document(tenantId)
                .get()
                .await()
            if (doc.exists()) mapTenant(doc) else null
        } catch (e: Exception) {
            Logger.e("Error fetching tenant $tenantId", e)
            null
        }
    }

    /** Mantiene Android sincronizado con los mismos cambios que recibe la Web. */
    fun observeActiveTenants(): Flow<List<Tenant>> = callbackFlow {
        val listener = firestore.collection(FirestoreCollections.TENANTS)
            .whereEqualTo("status", "active")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Logger.e("Error observing active tenants", error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.mapNotNull(::mapTenant).orEmpty())
            }
        awaitClose { listener.remove() }
    }

    fun observeTenant(tenantId: String): Flow<Tenant?> = callbackFlow {
        if (tenantId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val listener = firestore.collection(FirestoreCollections.TENANTS)
            .document(tenantId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Logger.e("Error observing tenant $tenantId", error)
                    trySend(null)
                    return@addSnapshotListener
                }
                trySend(snapshot?.takeIf { it.exists() }?.let(::mapTenant))
            }
        awaitClose { listener.remove() }
    }

    private fun mapTenant(doc: com.google.firebase.firestore.DocumentSnapshot): Tenant? {
        return try {
            Tenant.fromMap(doc.id, doc.data.orEmpty())
        } catch (e: Exception) {
            Logger.e("Error deserializing tenant ${doc.id}", e)
            null
        }
    }
}
