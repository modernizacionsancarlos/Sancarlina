package com.example.sancarlina.data.repository

import com.example.sancarlina.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Area(
    val id: String = "",
    val name: String = "",
    val description: String = ""
)

class AreasRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private var cachedAreas: List<Area>? = null

    suspend fun getAreas(): List<Area> {
        cachedAreas?.let { return it }

        return try {
            val snapshot = firestore.collection(FirestoreCollections.AREAS).get().await()
            val areas = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Area::class.java)?.copy(id = doc.id)
            }
            cachedAreas = areas
            areas
        } catch (e: Exception) {
            emptyList()
        }
    }
}
