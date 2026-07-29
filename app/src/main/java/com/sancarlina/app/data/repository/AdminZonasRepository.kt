package com.sancarlina.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.tasks.await

class AdminZonasRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
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
        return areasRepo.ensureSuggestedAreas()
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
            docRef.set(data).await()
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
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
