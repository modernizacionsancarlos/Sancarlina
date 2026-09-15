package com.sancarlina.app.ui.features.forms

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/**
 * Crea el destino donde la aplicación de cámara del sistema escribe la foto.
 *
 * El intent de captura no puede escribir en el almacenamiento privado de la app,
 * así que se le entrega un content:// generado por FileProvider sobre un archivo
 * de la caché. Al adjuntarse, la foto se copia al almacenamiento definitivo del
 * envío, por lo que este archivo queda descartable.
 */
internal fun createCameraCaptureUri(context: Context): Uri {
    val directory = File(context.cacheDir, CAMERA_CACHE_DIRECTORY)
    check(directory.mkdirs() || directory.isDirectory) {
        "No se pudo preparar el almacenamiento temporal de la cámara."
    }
    val file = File(directory, "captura_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

private const val CAMERA_CACHE_DIRECTORY = "camera"
