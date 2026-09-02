package com.sancarlina.app.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.OpenableColumns
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageMetadata
import com.sancarlina.app.data.local.OfflineFormsStore
import com.sancarlina.app.data.models.FormField
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.models.OfflineAttachment
import com.sancarlina.app.data.models.OfflineSubmission
import com.sancarlina.app.data.models.QueuedSubmissionResult
import com.sancarlina.app.data.models.SubmissionSyncStatus
import com.sancarlina.app.data.models.SyncSummary
import com.sancarlina.app.data.remote.FirestoreCollections
import com.sancarlina.app.work.FormSyncScheduler
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import java.util.UUID

class OfflineSubmissionsRepository(
    context: Context,
    private val store: OfflineFormsStore,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {
    private val appContext = context.applicationContext
    private val syncMutex = Mutex()

    val submissions: StateFlow<List<OfflineSubmission>> = store.submissions

    fun getSubmission(localId: String): OfflineSubmission? = store.getSubmission(localId)

    fun getAttachments(localId: String): List<OfflineAttachment> = store.getAttachments(localId)

    suspend fun enqueueAndTrySync(
        schema: FormSchema,
        values: Map<String, Any?>,
        attachmentUris: Map<String, List<Uri>>
    ): QueuedSubmissionResult {
        val userId = auth.currentUser?.uid
            ?: throw IllegalStateException("Iniciá sesión para guardar y enviar el formulario.")
        val localId = UUID.randomUUID().toString()
        val createdAt = System.currentTimeMillis()
        val attachmentDirectory = File(appContext.filesDir, "$ATTACHMENT_DIRECTORY/$localId")

        val attachments = try {
            copyAttachments(
                submissionId = localId,
                userId = userId,
                fields = schema.fields.associateBy { it.id },
                attachmentUris = attachmentUris,
                targetDirectory = attachmentDirectory
            )
        } catch (error: Exception) {
            attachmentDirectory.deleteRecursively()
            throw error
        }

        val submission = OfflineSubmission(
            localId = localId,
            formId = schema.id,
            formTitle = schema.title,
            userId = userId,
            data = values.filterValues { it != null },
            status = SubmissionSyncStatus.PENDING,
            createdAt = createdAt,
            updatedAt = createdAt,
            attemptCount = 0,
            lastError = null,
            remoteId = null
        )

        try {
            store.insertSubmission(submission, attachments)
        } catch (error: Exception) {
            attachmentDirectory.deleteRecursively()
            throw error
        }

        FormSyncScheduler.enqueue(appContext)

        if (hasValidatedConnection()) {
            syncSubmission(localId)
        }

        return QueuedSubmissionResult(
            localId = localId,
            status = store.getSubmission(localId)?.status ?: SubmissionSyncStatus.PENDING
        )
    }

    suspend fun updateAndTrySync(
        localId: String,
        schema: FormSchema,
        values: Map<String, Any?>,
        replacementAttachmentUris: Map<String, List<Uri>>,
        clearedAttachmentFields: Set<String>
    ): QueuedSubmissionResult {
        val existing = store.getSubmission(localId)
            ?: throw IllegalArgumentException("No se encontró el envío local para editar.")
        val userId = auth.currentUser?.uid
            ?: throw IllegalStateException("Iniciá sesión para editar y enviar el formulario.")
        require(existing.userId == userId) { "Este envío pertenece a otra cuenta." }
        require(existing.formId == schema.id) { "El formulario no coincide con el envío guardado." }
        check(existing.status != SubmissionSyncStatus.SENDING) {
            "Esperá a que termine la sincronización antes de editar."
        }

        val targetDirectory = File(appContext.filesDir, "$ATTACHMENT_DIRECTORY/$localId")
        val replacementAttachments = copyAttachments(
            submissionId = localId,
            userId = userId,
            fields = schema.fields.associateBy { it.id },
            attachmentUris = replacementAttachmentUris,
            targetDirectory = targetDirectory
        )
        val replacedFields = clearedAttachmentFields + replacementAttachmentUris.keys
        val oldPaths = try {
            store.updateSubmissionContent(
                localId = localId,
                data = values.filterValues { it != null },
                replacementAttachments = replacementAttachments,
                replacedAttachmentFields = replacedFields
            )
        } catch (error: Exception) {
            replacementAttachments.forEach { File(it.localPath).delete() }
            throw error
        }
        val replacementPaths = replacementAttachments.mapTo(mutableSetOf()) { it.localPath }
        oldPaths.filterNot(replacementPaths::contains).forEach { File(it).delete() }

        FormSyncScheduler.enqueue(appContext)
        if (hasValidatedConnection()) syncSubmission(localId)
        return QueuedSubmissionResult(
            localId = localId,
            status = store.getSubmission(localId)?.status ?: SubmissionSyncStatus.PENDING
        )
    }

    suspend fun syncPending(): SyncSummary = syncMutex.withLock {
        var sent = 0
        var failed = 0
        var permanentFailures = 0

        store.getSyncableSubmissions().forEach { submission ->
            when (syncSubmissionLocked(submission.localId)) {
                SyncAttempt.SENT -> sent++
                SyncAttempt.TRANSIENT_FAILURE -> failed++
                SyncAttempt.PERMANENT_FAILURE -> {
                    failed++
                    permanentFailures++
                }
                SyncAttempt.SKIPPED -> Unit
            }
        }

        SyncSummary(sent, failed, permanentFailures)
    }

    suspend fun syncSubmission(localId: String): SubmissionSyncStatus = syncMutex.withLock {
        syncSubmissionLocked(localId)
        store.getSubmission(localId)?.status ?: SubmissionSyncStatus.ERROR
    }

    fun retry(localId: String) {
        store.markPending(localId)
        FormSyncScheduler.enqueue(appContext)
    }

    fun retryAll() {
        store.submissions.value
            .filter { it.status == SubmissionSyncStatus.ERROR }
            .forEach { store.markPending(it.localId) }
        FormSyncScheduler.enqueue(appContext)
    }

    fun scheduleSync() = FormSyncScheduler.enqueue(appContext)

    fun hasValidatedConnection(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private suspend fun syncSubmissionLocked(localId: String): SyncAttempt {
        val submission = store.getSubmission(localId) ?: return SyncAttempt.SKIPPED
        if (submission.status == SubmissionSyncStatus.SENT) return SyncAttempt.SKIPPED

        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null || currentUserId != submission.userId) {
            store.markError(
                localId,
                if (currentUserId == null) {
                    "Iniciá sesión nuevamente para sincronizar este formulario."
                } else {
                    "Este formulario pertenece a otra cuenta. Iniciá sesión con la cuenta original."
                }
            )
            return SyncAttempt.PERMANENT_FAILURE
        }

        store.markSending(localId)
        return try {
            val attachmentValues = uploadAttachments(submission)
            val document = firestore.collection(FirestoreCollections.SUBMISSIONS).document(localId)

            val existing = withTimeout(REMOTE_OPERATION_TIMEOUT_MS) {
                document.get(Source.SERVER).await()
            }
            val payload = submission.data.toMutableMap().apply {
                putAll(attachmentValues)
                put("form_id", submission.formId)
                put("form_title", submission.formTitle)
                put("created_by", submission.userId)
                put("created_at", Timestamp(Date(submission.createdAt)))
                put("client_updated_at", Timestamp(Date(submission.updatedAt)))
                put("client_submission_id", submission.localId)
                put("synced_at", FieldValue.serverTimestamp())
                put("status", "pending")
            }
            if (existing.exists()) {
                val existingOwner = existing.getString("created_by")
                val existingClientId = existing.getString("client_submission_id")
                if (existingOwner != submission.userId || existingClientId != localId) {
                    throw PermanentSyncException("El identificador remoto está ocupado por otro envío.")
                }
                val remoteContentUpdatedAt = existing.getTimestamp("client_updated_at")?.toDate()?.time
                if (remoteContentUpdatedAt == null || remoteContentUpdatedAt < submission.updatedAt) {
                    withTimeout(REMOTE_OPERATION_TIMEOUT_MS) {
                        document.set(payload).await()
                    }
                }
            } else {
                withTimeout(REMOTE_OPERATION_TIMEOUT_MS) {
                    document.set(payload).await()
                }
            }

            store.markSent(localId, localId)
            deleteLocalAttachmentFiles(localId)
            SyncAttempt.SENT
        } catch (error: Exception) {
            val message = userFacingError(error)
            store.markError(localId, message)
            if (isPermanent(error)) SyncAttempt.PERMANENT_FAILURE else SyncAttempt.TRANSIENT_FAILURE
        }
    }

    private suspend fun uploadAttachments(submission: OfflineSubmission): Map<String, Any> {
        val attachments = store.getAttachments(submission.localId)
        if (attachments.isEmpty()) return emptyMap()

        val uploaded = attachments.map { attachment ->
            if (!attachment.remoteUrl.isNullOrBlank()) {
                attachment
            } else {
                val file = File(attachment.localPath)
                if (!file.isFile) {
                    throw PermanentSyncException("No se encuentra el archivo ${attachment.displayName} en el dispositivo.")
                }
                try {
                    val reference = storage.reference.child(attachment.storagePath)
                    val metadata = StorageMetadata.Builder()
                        .setContentType(attachment.mimeType)
                        .setCustomMetadata("submissionId", submission.localId)
                        .setCustomMetadata("fieldId", attachment.fieldId)
                        .build()
                    withTimeout(REMOTE_OPERATION_TIMEOUT_MS) {
                        reference.putFile(Uri.fromFile(file), metadata).await()
                    }
                    val remoteUrl = withTimeout(REMOTE_OPERATION_TIMEOUT_MS) {
                        reference.downloadUrl.await().toString()
                    }
                    store.updateAttachmentRemoteUrl(attachment.id, remoteUrl)
                    attachment.copy(remoteUrl = remoteUrl, lastError = null)
                } catch (error: Exception) {
                    store.updateAttachmentError(attachment.id, userFacingError(error))
                    throw error
                }
            }
        }

        return uploaded.groupBy { it.fieldId }.mapValues { (_, fieldAttachments) ->
            val urls = fieldAttachments.sortedBy { it.position }.mapNotNull { it.remoteUrl }
            if (urls.size == 1) urls.first() else urls
        }
    }

    private fun copyAttachments(
        submissionId: String,
        userId: String,
        fields: Map<String, FormField>,
        attachmentUris: Map<String, List<Uri>>,
        targetDirectory: File
    ): List<OfflineAttachment> {
        if (attachmentUris.isEmpty()) return emptyList()
        check(targetDirectory.mkdirs() || targetDirectory.isDirectory) {
            "No se pudo preparar el almacenamiento local de adjuntos."
        }
        val resolver = appContext.contentResolver
        val createdFiles = mutableListOf<File>()
        return try {
            buildList {
                attachmentUris.forEach { (fieldId, uris) ->
                    val field = fields[fieldId]
                    val limit = field?.maxImages?.coerceAtLeast(1) ?: uris.size.coerceAtLeast(1)
                    uris.take(limit).forEachIndexed { index, uri ->
                    val mimeType = resolver.getType(uri) ?: "application/octet-stream"
                    val displayName = resolver.query(
                        uri,
                        arrayOf(OpenableColumns.DISPLAY_NAME),
                        null,
                        null,
                        null
                    )?.use { cursor ->
                        if (cursor.moveToFirst()) cursor.getString(0) else null
                    } ?: "archivo_${index + 1}"
                    val safeName = sanitizeFileName(displayName, index)
                    val safeFieldId = sanitizeFileName(fieldId, index)
                    val localToken = UUID.randomUUID().toString().take(8)
                    val attachmentId = "$submissionId-$safeFieldId-$index-$localToken"
                    val target = File(targetDirectory, "${safeFieldId}_${index}_${localToken}_$safeName")
                        val maxBytes = if (mimeType.startsWith("image/")) MAX_IMAGE_BYTES else MAX_FILE_BYTES
                        createdFiles += target
                        resolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(target).use { output ->
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                            var total = 0L
                            while (true) {
                                val read = input.read(buffer)
                                if (read < 0) break
                                total += read
                                if (total > maxBytes) {
                                    throw IllegalArgumentException(
                                        if (mimeType.startsWith("image/")) {
                                            "La imagen $displayName supera el límite de 5 MB."
                                        } else {
                                            "El archivo $displayName supera el límite de 10 MB."
                                        }
                                    )
                                }
                                output.write(buffer, 0, read)
                            }
                            }
                        } ?: throw IllegalArgumentException("No se pudo leer el archivo $displayName.")

                        add(
                            OfflineAttachment(
                            id = attachmentId,
                            submissionId = submissionId,
                            fieldId = fieldId,
                            displayName = displayName,
                            mimeType = mimeType,
                            localPath = target.absolutePath,
                            storagePath = "submissions/$userId/$submissionId/$safeFieldId/${index}_$safeName",
                            position = index,
                            remoteUrl = null,
                            lastError = null
                            )
                        )
                    }
                }
            }
        } catch (error: Exception) {
            createdFiles.forEach { it.delete() }
            throw error
        }
    }

    private fun deleteLocalAttachmentFiles(submissionId: String) {
        val directory = File(appContext.filesDir, "$ATTACHMENT_DIRECTORY/$submissionId")
        if (directory.isDirectory) directory.deleteRecursively()
    }

    private fun sanitizeFileName(displayName: String, index: Int): String {
        val sanitized = displayName
            .replace(Regex("[^A-Za-z0-9._-]"), "_")
            .trim('.', '_')
            .take(80)
        return sanitized.ifBlank { "archivo_${index + 1}" }
    }

    private fun userFacingError(error: Exception): String = when (error) {
        is PermanentSyncException -> error.message.orEmpty()
        is FirebaseFirestoreException -> when (error.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Firebase rechazó el envío. Verificá la sesión y los permisos."
            FirebaseFirestoreException.Code.UNAUTHENTICATED -> "La sesión venció. Iniciá sesión nuevamente."
            else -> error.localizedMessage ?: "No se pudo enviar la respuesta a Firestore."
        }
        is StorageException -> when (error.errorCode) {
            StorageException.ERROR_NOT_AUTHENTICATED -> "La sesión venció antes de subir los adjuntos."
            StorageException.ERROR_NOT_AUTHORIZED -> "Storage rechazó el adjunto. Verificá los permisos publicados."
            else -> error.localizedMessage ?: "No se pudo subir un adjunto."
        }
        else -> error.localizedMessage ?: "La sincronización falló. Se volverá a intentar."
    }.take(500)

    private fun isPermanent(error: Exception): Boolean = when (error) {
        is PermanentSyncException, is IllegalArgumentException -> true
        is FirebaseFirestoreException -> error.code in setOf(
            FirebaseFirestoreException.Code.PERMISSION_DENIED,
            FirebaseFirestoreException.Code.UNAUTHENTICATED,
            FirebaseFirestoreException.Code.INVALID_ARGUMENT
        )
        is StorageException -> error.errorCode in setOf(
            StorageException.ERROR_NOT_AUTHENTICATED,
            StorageException.ERROR_NOT_AUTHORIZED,
            StorageException.ERROR_INVALID_CHECKSUM
        )
        else -> false
    }

    private enum class SyncAttempt { SENT, TRANSIENT_FAILURE, PERMANENT_FAILURE, SKIPPED }
    private class PermanentSyncException(message: String) : Exception(message)

    companion object {
        private const val ATTACHMENT_DIRECTORY = "offline_form_attachments"
        private const val MAX_IMAGE_BYTES = 5L * 1024L * 1024L
        private const val MAX_FILE_BYTES = 10L * 1024L * 1024L
        private const val REMOTE_OPERATION_TIMEOUT_MS = 60_000L
    }
}
