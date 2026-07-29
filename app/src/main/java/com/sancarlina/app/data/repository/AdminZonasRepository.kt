package com.sancarlina.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.tasks.await

class AdminZonasRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getAllAreas(): Result<List<Area>> {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.AREAS)
                .get()
                .await()

            val areas = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Area::class.java)?.copy(id = doc.id)
            }
            Result.success(areas)
        } catch (e: Exception) {
            Result.failure(e)
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
                "description" to area.description
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
