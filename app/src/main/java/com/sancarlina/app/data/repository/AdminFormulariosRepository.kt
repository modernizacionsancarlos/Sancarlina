package com.sancarlina.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.models.FormTemplate
import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.data.remote.FirestoreCollections
import com.sancarlina.app.data.templates.BuiltinFormTemplates
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class SubmissionAdmin(
    val id: String = "",
    val form_id: String = "",
    val form_title: String = "",
    val created_by: String = "",
    val created_at: Timestamp? = null,
    val status: String = "pending",
    val data: Map<String, Any> = emptyMap()
)

class AdminFormulariosRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // --- FORM SCHEMAS ---
    suspend fun getAllSchemas(): Result<List<FormSchema>> {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.FORM_SCHEMAS)
                .get()
                .await()

            val schemas = snapshot.documents.mapNotNull { doc ->
                try {
                    FormSchema.fromMap(doc.id, doc.data.orEmpty())
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(schemas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveSchema(schema: FormSchema): Result<String> {
        return try {
            val collection = firestore.collection(FirestoreCollections.FORM_SCHEMAS)
            val docRef = if (schema.id.isNotBlank()) {
                collection.document(schema.id)
            } else {
                collection.document()
            }

            val tenantId = schema.tenantId.ifBlank { schema.tenant_id }
                .ifBlank { FirestoreCollections.DEFAULT_TENANT_ID }
            val serializedFields = schema.fields.map { field ->
                mapOf(
                    "id" to field.id,
                    "type" to field.type,
                    "label" to field.label,
                    "required" to field.required,
                    "tenantMapping" to field.tenantMapping,
                    "tenant_mapping" to field.tenantMapping,
                    "options" to field.options,
                    "placeholder" to field.placeholder,
                    "helpText" to field.helpText,
                    "help_text" to field.helpText,
                    "description" to field.description,
                    "maxImages" to field.maxImages,
                    "max_images" to field.maxImages,
                    "allowMultiple" to field.allowMultiple,
                    "allow_multiple" to field.allowMultiple
                ).filterValues { it != null }
            }
            val schemaData = mutableMapOf<String, Any?>(
                "title" to schema.title,
                "description" to schema.description,
                "tenantId" to tenantId,
                "tenant_id" to tenantId,
                "submitUrl" to schema.submitUrl,
                "submit_url" to schema.submitUrl,
                "is_public" to schema.isPublic,
                "accepts_responses" to schema.acceptsResponses,
                "status" to schema.status,
                "fields" to serializedFields,
                "formPurpose" to schema.formPurpose,
                "form_purpose" to schema.formPurpose,
                "templateSource" to schema.templateSource,
                "template_source" to schema.templateSource,
                "templateCategory" to schema.templateCategory,
                "template_category" to schema.templateCategory,
                "municipalityNotes" to schema.municipalityNotes,
                "municipality_notes" to schema.municipalityNotes,
                "allowedRoles" to schema.allowedRoles,
                "allowed_roles" to schema.allowedRoles,
                "assignedUserIds" to schema.assignedUserIds,
                "assigned_user_ids" to schema.assignedUserIds,
                "fieldRegistrationEnabled" to schema.fieldRegistrationEnabled,
                "field_registration_enabled" to schema.fieldRegistrationEnabled
            )

            docRef.set(schemaData.filterValues { it != null }, SetOptions.merge()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun togglePublic(schemaId: String, isPublic: Boolean): Result<Unit> {
        return try {
            firestore.collection(FirestoreCollections.FORM_SCHEMAS)
                .document(schemaId)
                .update("is_public", isPublic)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleAcceptsResponses(schemaId: String, acceptsResponses: Boolean): Result<Unit> {
        return try {
            firestore.collection(FirestoreCollections.FORM_SCHEMAS)
                .document(schemaId)
                .update("accepts_responses", acceptsResponses)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSchema(schemaId: String): Result<Unit> {
        return try {
            firestore.collection(FirestoreCollections.FORM_SCHEMAS)
                .document(schemaId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- FORM TEMPLATES ---
    suspend fun getAllTemplates(): Result<List<FormTemplate>> {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.FORM_TEMPLATES)
                .get()
                .await()

            val remoteTemplates = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(FormTemplate::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    null
                }
            }

            val combined = (BuiltinFormTemplates.ALL_TEMPLATES + remoteTemplates)
                .distinctBy { it.id }

            Result.success(combined)
        } catch (e: Exception) {
            Result.success(BuiltinFormTemplates.ALL_TEMPLATES)
        }
    }

    // --- SUBMISSIONS & ACEPTACIONES ---
    suspend fun getAllSubmissions(): Result<List<SubmissionAdmin>> {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.SUBMISSIONS)
                .get()
                .await()

            val submissions = snapshot.documents.mapNotNull(::parseSubmission)
            Result.success(submissions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeAllSubmissions(): Flow<List<SubmissionAdmin>> = callbackFlow {
        val listener = firestore.collection(FirestoreCollections.SUBMISSIONS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val submissions = snapshot?.documents
                    ?.mapNotNull(::parseSubmission)
                    ?.sortedByDescending { it.created_at?.seconds ?: 0L }
                    .orEmpty()
                trySend(submissions)
            }
        awaitClose { listener.remove() }
    }

    private fun parseSubmission(doc: com.google.firebase.firestore.DocumentSnapshot): SubmissionAdmin? =
        runCatching {
            @Suppress("UNCHECKED_CAST")
            val nestedData = doc.get("data") as? Map<String, Any>
            val responseData = nestedData ?: doc.data.orEmpty()
                .filterKeys { it !in SUBMISSION_METADATA_KEYS }
                .filterValues { it != null }
                .mapValues { it.value as Any }
            SubmissionAdmin(
                id = doc.id,
                form_id = doc.getString("form_id") ?: "",
                form_title = doc.getString("form_title") ?: "",
                created_by = doc.getString("created_by") ?: "",
                created_at = doc.getTimestamp("created_at"),
                status = doc.getString("status") ?: "pending",
                data = responseData
            )
        }.getOrNull()

    suspend fun updateSubmissionStatus(submissionId: String, status: String): Result<Unit> {
        return try {
            firestore.collection(FirestoreCollections.SUBMISSIONS)
                .document(submissionId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Convierte una submission aprobada en un documento activo en `tenants`.
     */
    suspend fun publishSubmissionToTenant(
        submission: SubmissionAdmin,
        schema: FormSchema?
    ): Result<String> {
        return try {
            val subData = submission.data
            val fields = schema?.fields ?: emptyList()

            var name = ""
            var industry = ""
            var areaId = ""
            var description = ""
            var contactEmail = ""
            var contactPhone = ""
            var address = ""
            var geoCoordinates = ""
            var coverUrl = ""
            val galleryUrls = mutableListOf<String>()

            for (field in fields) {
                val rawVal = subData[field.id] ?: continue
                when (field.tenantMapping) {
                    "name" -> name = rawVal.toString().trim()
                    "industry" -> industry = rawVal.toString().trim()
                    "area_id" -> areaId = rawVal.toString().trim()
                    "description" -> description = rawVal.toString().trim()
                    "contact_email" -> contactEmail = rawVal.toString().trim()
                    "contact_phone" -> contactPhone = rawVal.toString().trim()
                    "address" -> address = rawVal.toString().trim()
                    "geo_coordinates" -> geoCoordinates = rawVal.toString().trim()
                    "cover_url", "image_url" -> coverUrl = rawVal.toString().trim()
                    "gallery" -> {
                        if (rawVal is List<*>) {
                            galleryUrls.addAll(rawVal.mapNotNull { it?.toString() })
                        } else {
                            galleryUrls.add(rawVal.toString())
                        }
                    }
                }
            }

            if (name.isBlank()) {
                name = subData["field_nombre"]?.toString()
                    ?: subData["name"]?.toString()
                    ?: "Comercio desde Formulario ${submission.id.take(6)}"
            }

            val newTenantData = mapOf(
                "name" to name,
                "industry" to if (industry.isNotBlank()) industry else "General",
                "status" to "active",
                "tenantId" to FirestoreCollections.DEFAULT_TENANT_ID,
                "tenant_id" to FirestoreCollections.DEFAULT_TENANT_ID,
                "area_id" to areaId,
                "description" to description,
                "contact_email" to contactEmail,
                "contact_phone" to contactPhone,
                "address" to address,
                "geo_coordinates" to geoCoordinates,
                "cover_url" to coverUrl,
                "image_url" to coverUrl,
                "gallery" to galleryUrls.distinct(),
                "rating" to 0.0,
                "reviews_count" to 0
            )

            val tenantRef = firestore.collection(FirestoreCollections.TENANTS).document()
            tenantRef.set(newTenantData).await()

            // Marcar submission como aprobada
            updateSubmissionStatus(submission.id, "approved")

            Result.success(tenantRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private val SUBMISSION_METADATA_KEYS = setOf(
            "form_id",
            "form_title",
            "created_by",
            "created_at",
            "client_updated_at",
            "client_submission_id",
            "synced_at",
            "status",
            "data"
        )
    }
}
