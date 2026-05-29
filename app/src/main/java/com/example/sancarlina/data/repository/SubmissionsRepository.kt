package com.example.sancarlina.data.repository

import com.example.sancarlina.data.remote.FirestoreCollections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SubmissionsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun submitForm(formId: String, data: Map<String, Any>): Result<String> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
        
        val submission = data.toMutableMap().apply {
            put("form_id", formId)
            put("created_by", uid)
            put("created_at", com.google.firebase.Timestamp.now())
        }

        return try {
            val docRef = firestore.collection(FirestoreCollections.SUBMISSIONS)
                .add(submission)
                .await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
