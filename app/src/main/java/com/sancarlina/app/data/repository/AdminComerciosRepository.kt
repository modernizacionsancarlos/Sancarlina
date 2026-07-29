package com.sancarlina.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.tasks.await

class AdminComerciosRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getAllTenants(): Result<List<Tenant>> {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.TENANTS)
                .get()
                .await()

            val tenants = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Tenant::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(tenants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveTenant(tenant: Tenant): Result<String> {
        return try {
            val collection = firestore.collection(FirestoreCollections.TENANTS)
            val docRef = if (tenant.id.isNotBlank()) {
                collection.document(tenant.id)
            } else {
                collection.document()
            }

            val tenantData = mutableMapOf<String, Any?>(
                "name" to tenant.name,
                "industry" to tenant.industry,
                "status" to tenant.status,
                "tenantId" to FirestoreCollections.DEFAULT_TENANT_ID,
                "tenant_id" to FirestoreCollections.DEFAULT_TENANT_ID,
                "area_id" to tenant.area_id,
                "description" to tenant.description,
                "contact_email" to tenant.contact_email,
                "contact_phone" to tenant.contact_phone,
                "address" to tenant.address,
                "geo_coordinates" to tenant.geo_coordinates,
                "cover_url" to tenant.cover_url,
                "image_url" to tenant.image_url,
                "gallery" to tenant.gallery,
                "rating" to tenant.rating,
                "reviews_count" to tenant.reviews_count
            )

            docRef.set(tenantData.filterValues { it != null }).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTenantStatus(tenantId: String, newStatus: String): Result<Unit> {
        return try {
            firestore.collection(FirestoreCollections.TENANTS)
                .document(tenantId)
                .update("status", newStatus)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTenant(tenantId: String): Result<Unit> {
        return try {
            firestore.collection(FirestoreCollections.TENANTS)
                .document(tenantId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
