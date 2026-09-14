package com.sancarlina.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sancarlina.app.data.remote.FirestoreCollections
import com.sancarlina.app.data.cache.CacheDataset
import kotlinx.coroutines.tasks.await

class AdminZonasRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val invalidation: CatalogInvalidationRepository? = null
) {
    private val areasRepo = AreasRepository(firestore)

    suspend fun getAllAreas(): Result<List<Area>> {
        return try {
            val list = areasRepo.getAreas()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun ensureSuggestedAreas(): Result<Int> {
        return areasRepo.ensureSuggestedAreas().also { result ->
            if (result.getOrDefault(0) > 0) invalidation?.notifyChanged(CacheDataset.AREAS)
        }
    }

    suspend fun saveArea(area: Area): Result<String> {
        return try {
            val collection = firestore.collection(FirestoreCollections.AREAS)
            val docRef = if (area.id.isNotBlank()) {
                collection.document(area.id)
            } else {
                collection.document()
            }

            val data = mapOf(
                "name" to area.name,
                "slug" to area.slug.ifBlank { area.name.lowercase().replace(" ", "-") },
                "description" to area.description,
                "order" to area.order,
                "category" to area.category,
                "icon" to area.icon,
                "active" to area.active
            )
            docRef.set(data, SetOptions.merge()).await()
            invalidation?.notifyChanged(CacheDataset.AREAS)
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteArea(areaId: String): Result<Unit> {
        return try {
            firestore.collection(FirestoreCollections.AREAS)
                .document(areaId)
                .delete()
                .await()
            invalidation?.notifyChanged(CacheDataset.AREAS)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
