package com.sancarlina.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.tasks.await

class AdminBeneficiosRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getAllBenefits(): Result<List<Benefit>> {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.BENEFITS)
                .get()
                .await()

            val benefits = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Benefit::class.java)?.copy(id = doc.id)
            }
            Result.success(benefits)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveBenefit(benefit: Benefit): Result<String> {
        return try {
            val collection = firestore.collection(FirestoreCollections.BENEFITS)
            val docRef = if (benefit.id.isNotBlank()) {
                collection.document(benefit.id)
            } else {
                collection.document()
            }

            val data = mapOf(
                "title" to benefit.title,
                "cost" to benefit.cost,
                "points_cost" to (if (benefit.points_cost > 0) benefit.points_cost else benefit.cost),
                "description" to benefit.description,
                "cover_url" to benefit.cover_url,
                "industry" to benefit.industry,
                "active" to benefit.active
            )
            docRef.set(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleActive(benefitId: String, active: Boolean): Result<Unit> {
        return try {
            firestore.collection(FirestoreCollections.BENEFITS)
                .document(benefitId)
                .update("active", active)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBenefit(benefitId: String): Result<Unit> {
        return try {
            firestore.collection(FirestoreCollections.BENEFITS)
                .document(benefitId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
