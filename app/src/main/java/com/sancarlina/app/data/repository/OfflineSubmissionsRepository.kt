package com.sancarlina.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

        if (hasUsableConnection()) {
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
        if (hasUsableConnection()) syncSubmission(localId)
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

    /**
     * Indica si hay una red por la que valga la pena intentar el envío.
     *
     * Deliberadamente NO exige NET_CAPABILITY_VALIDATED. Android marca esa
     * capacidad recién cuando una sonda contra sus servidores responde bien, y con
     * señal móvil débil dentro de un local esa sonda falla o tarda aunque los datos
     * funcionen. Exigirla hacía que la app se declarara sin conexión con el teléfono
     * navegando, y los relevamientos quedaban sin intentar.
     *
     * Si la red resulta inservible, el intento falla y la cola lo reintenta: para
     * eso existe la cola. Un intento fallido cuesta mucho menos que un envío que
     * nunca se prueba.
     */
    fun hasUsableConnection(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * true cuando el Ahorro de datos de Android está activo sobre una red medida.
     *
     * En ese estado la sincronización manual sigue funcionando, porque la app está
     * en primer plano, pero el trabajo en segundo plano queda diferido hasta que
     * aparezca una red no medida. Es lo que produce la impresión de que los
     * relevamientos "solo suben con Wi-Fi".
     */
    fun isBackgroundDataRestricted(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.isActiveNetworkMetered &&
            connectivityManager.restrictBackgroundStatus ==
                ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED
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
            withFreshTokenRetry { pushSubmission(submission) }
            store.markSent(localId, localId)
            deleteLocalAttachmentFiles(localId)
            SyncAttempt.SENT
        } catch (error: Exception) {
            val message = userFacingError(error)
            store.markError(localId, message)
            if (isPermanent(error)) SyncAttempt.PERMANENT_FAILURE else SyncAttempt.TRANSIENT_FAILURE
        }
    }

    /**
     * Sube los adjuntos y escribe el documento en Firestore.
     *
     * El id del documento es el UUID generado en el dispositivo, de modo que
     * reenviar el mismo formulario nunca duplica un registro en el panel web.
     */
    private suspend fun pushSubmission(submission: OfflineSubmission) {
        val localId = submission.localId
        val attachmentValues = uploadAttachments(submission)
        val document = firestore.collection(FirestoreCollections.SUBMISSIONS).document(localId)
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

        // En el primer intento el documento no puede existir en el servidor: el id
        // es un UUID recién generado. Leerlo cuesta una lectura facturada por envío
        // y agrega un modo de falla propio, porque una regla de lectura restrictiva
        // rechaza un documento inexistente y el envío queda marcado como error sin
        // que se haya intentado escribir.
        val remoteCopyMayExist = submission.remoteId != null || submission.attemptCount > 0
        if (!remoteCopyMayExist) {
            withTimeout(REMOTE_OPERATION_TIMEOUT_MS) { document.set(payload).await() }
            return
        }

        val existing = withTimeout(REMOTE_OPERATION_TIMEOUT_MS) {
            document.get(Source.SERVER).await()
        }
        if (!existing.exists()) {
            withTimeout(REMOTE_OPERATION_TIMEOUT_MS) { document.set(payload).await() }
            return
        }

        val existingOwner = existing.getString("created_by")
        val existingClientId = existing.getString("client_submission_id")
        if (existingOwner != submission.userId || existingClientId != localId) {
            throw PermanentSyncException("El identificador remoto está ocupado por otro envío.")
        }
        val remoteContentUpdatedAt = existing.getTimestamp("client_updated_at")?.toDate()?.time
        if (remoteContentUpdatedAt == null || remoteContentUpdatedAt < submission.updatedAt) {
            withTimeout(REMOTE_OPERATION_TIMEOUT_MS) { document.set(payload).await() }
        }
    }

    /**
     * Ejecuta [block] y, si el servidor responde por falta de permisos, renueva el
     * ID token y reintenta una sola vez.
     *
     * Los claims de rol se asignan desde Cloud Functions. Un registrador que inició
     * sesión antes de recibir su claim arrastra un token sin permisos hasta que
     * caduca, una hora después. Sin este reintento cada relevamiento cargado en ese
     * intervalo se marca como error permanente y solo vuelve con acción manual.
     */
    private suspend fun <T> withFreshTokenRetry(block: suspend () -> T): T = try {
        block()
    } catch (error: Exception) {
        val user = auth.currentUser
        if (!isAuthorizationError(error) || user == null) throw error
        runCatching { withTimeout(REMOTE_OPERATION_TIMEOUT_MS) { user.getIdToken(true).await() } }
            .getOrElse { throw error }
        block()
    }

    private fun isAuthorizationError(error: Exception): Boolean = when (error) {
        is FirebaseFirestoreException ->
            error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ||
                error.code == FirebaseFirestoreException.Code.UNAUTHENTICATED
        is StorageException ->
            error.errorCode == StorageException.ERROR_NOT_AUTHENTICATED ||
                error.errorCode == StorageException.ERROR_NOT_AUTHORIZED
        else -> false
    }

    private suspend fun uploadAttachments(submission: OfflineSubmission): Map<String, Any> {
        val attachments = store.getAttachments(submission.localId)
        if (attachments.isEmpty()) return emptyMap()

        // Los adjuntos se suben en paralelo. En serie, un relevamiento con tres
        // fotos pagaba tres veces la latencia de subida más tres consultas de URL,
        // y sobre datos móviles eso son decenas de segundos de espera.
        val uploaded = coroutineScope {
            attachments.map { attachment ->
                async { uploadAttachment(submission, attachment) }
            }.awaitAll()
        }

        return uploaded.groupBy { it.fieldId }.mapValues { (_, fieldAttachments) ->
            val urls = fieldAttachments.sortedBy { it.position }.mapNotNull { it.remoteUrl }
            if (urls.size == 1) urls.first() else urls
        }
    }

    private suspend fun uploadAttachment(
        submission: OfflineSubmission,
        attachment: OfflineAttachment
    ): OfflineAttachment {
        return if (!attachment.remoteUrl.isNullOrBlank()) {
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
                        val isImage = mimeType.startsWith("image/")
                        val maxBytes = if (isImage) MAX_SOURCE_IMAGE_BYTES else MAX_FILE_BYTES
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
                                            "La imagen $displayName supera el límite de 25 MB."
                                        } else {
                                            "El archivo $displayName supera el límite de 10 MB."
                                        }
                                    )
                                }
                                output.write(buffer, 0, read)
                            }
                            }
                        } ?: throw IllegalArgumentException("No se pudo leer el archivo $displayName.")

                        // Una foto de cámara moderna ronda los 4 a 12 MB. Subirla entera
                        // sobre datos móviles es la parte más lenta de la sincronización
                        // y el servidor no necesita esa resolución. Reducirla acá deja
                        // archivos de unos cientos de kilobytes.
                        val storedMimeType = if (isImage && downscaleImageInPlace(target)) {
                            "image/jpeg"
                        } else {
                            mimeType
                        }

                        add(
                            OfflineAttachment(
                            id = attachmentId,
                            submissionId = submissionId,
                            fieldId = fieldId,
                            displayName = displayName,
                            mimeType = storedMimeType,
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

    /**
     * Reduce la imagen del archivo indicado y la reescribe como JPEG.
     *
     * Devuelve true cuando la reescribió. Ante cualquier problema devuelve false y
     * deja el archivo original intacto: subir una foto grande es peor que subirla
     * reducida, pero mucho mejor que perder el relevamiento.
     */
    private fun downscaleImageInPlace(file: File): Boolean = runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        val largestSide = maxOf(bounds.outWidth, bounds.outHeight)
        if (largestSide <= 0) return@runCatching false

        var sampleSize = 1
        while (largestSide / (sampleSize * 2) >= MAX_IMAGE_DIMENSION) {
            sampleSize *= 2
        }
        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val decoded = BitmapFactory.decodeFile(file.absolutePath, decodeOptions)
            ?: return@runCatching false

        val scale = MAX_IMAGE_DIMENSION.toFloat() / maxOf(decoded.width, decoded.height)
        val scaled = if (scale < 1f) {
            Bitmap.createScaledBitmap(
                decoded,
                (decoded.width * scale).toInt().coerceAtLeast(1),
                (decoded.height * scale).toInt().coerceAtLeast(1),
                true
            )
        } else {
            decoded
        }

        // La cámara guarda la orientación en EXIF en lugar de rotar los píxeles.
        // Al recomprimir se pierde ese dato, así que hay que aplicarlo antes.
        val rotation = exifRotationDegrees(file)
        val oriented = if (rotation != 0f) {
            Bitmap.createBitmap(
                scaled,
                0,
                0,
                scaled.width,
                scaled.height,
                Matrix().apply { postRotate(rotation) },
                true
            )
        } else {
            scaled
        }

        FileOutputStream(file).use { output ->
            oriented.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, output)
        }
        true
    }.getOrDefault(false)

    private fun exifRotationDegrees(file: File): Float = runCatching {
        when (
            ExifInterface(file.absolutePath).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        ) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    }.getOrDefault(0f)

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
            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                "El servidor rechazó el envío por permisos. El relevamiento sigue guardado. " +
                    "Cerrá sesión, volvé a entrar y tocá Reintentar; si continúa, avisá al administrador."
            FirebaseFirestoreException.Code.UNAUTHENTICATED ->
                "La sesión venció. El relevamiento sigue guardado: iniciá sesión y tocá Reintentar."
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
        // Tope de lectura, no de subida: la imagen se reduce antes de enviarse.
        private const val MAX_SOURCE_IMAGE_BYTES = 25L * 1024L * 1024L
        private const val MAX_IMAGE_DIMENSION = 1600
        private const val IMAGE_QUALITY = 80
        private const val MAX_FILE_BYTES = 10L * 1024L * 1024L
        private const val REMOTE_OPERATION_TIMEOUT_MS = 60_000L
    }
}
