package com.sancarlina.app.data.remote

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.tasks.await
import java.io.InputStream

object FormImageUploader {

    private const val MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024L // 5 MB

    /**
     * Sube una o varias imágenes a Firebase Storage bajo la ruta:
     * submissions/{formId}/{fieldId}/{timestamp}_{index}.jpg
     *
     * Valida que cada archivo no supere 5 MB.
     * Retorna String (si es 1 sola imagen) o List<String> (si son varias).
     */
    suspend fun uploadImages(
        context: Context,
        formId: String,
        fieldId: String,
        imageUris: List<Uri>,
        maxImages: Int = 1,
        storage: FirebaseStorage = FirebaseStorage.getInstance()
    ): Any {
        if (imageUris.isEmpty()) return ""

        val validUris = imageUris.take(maxImages)
        val downloadUrls = mutableListOf<String>()

        val contentResolver = context.contentResolver

        for ((index, uri) in validUris.withIndex()) {
            // Verificar tamaño del archivo
            val fileSize = contentResolver.openFileDescriptor(uri, "r")?.use {
                it.statSize
            } ?: 0L

            if (fileSize > MAX_FILE_SIZE_BYTES) {
                throw IllegalArgumentException("La imagen supera el límite permitido de 5 MB.")
            }

            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val extension = when {
                mimeType.contains("png") -> "png"
                mimeType.contains("webp") -> "webp"
                else -> "jpg"
            }

            val timestamp = System.currentTimeMillis()
            val fileName = "${timestamp}_${index + 1}.$extension"
            val storagePath = "submissions/$formId/$fieldId/$fileName"

            val storageRef = storage.reference.child(storagePath)
            val metadata = StorageMetadata.Builder()
                .setContentType(mimeType)
                .build()

            val inputStream: InputStream = contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("No se pudo leer el archivo de imagen.")

            inputStream.use { stream ->
                storageRef.putStream(stream, metadata).await()
            }

            val downloadUrl = storageRef.downloadUrl.await().toString()
            downloadUrls.add(downloadUrl)
        }

        return if (downloadUrls.size == 1) {
            downloadUrls.first()
        } else {
            downloadUrls
        }
    }
}
