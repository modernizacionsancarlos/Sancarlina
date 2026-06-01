package com.sancarlina.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.sancarlina.app.BuildConfig
import com.sancarlina.app.MainActivity
import com.sancarlina.app.R
import com.sancarlina.app.SancarlinaApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class UpdateManager(private val context: Context) {

    private val client = OkHttpClient()
    private val CONFIG_URL = "https://raw.githubusercontent.com/franco-valenzuela/sancarlina-distribucion/main/config.json"
    private val CHANNEL_ID = "updates_channel"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Actualizaciones"
            val descriptionText = "Notificaciones de nuevas versiones de Sancarlina"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    suspend fun checkForUpdates(onUpdateAvailable: (apkUrl: String, notes: String) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(CONFIG_URL).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext
                    
                    val jsonData = response.body?.string() ?: return@withContext
                    val json = JSONObject(jsonData)
                    
                    val latestVersionCode = json.getInt("latestVersionCode")
                    val apkUrl = json.getString("apkUrl")
                    val releaseNotes = json.optString("releaseNotes", "Nueva versión disponible")

                    if (latestVersionCode > BuildConfig.VERSION_CODE) {
                        // SOLO mostramos notificación si la app NO está en primer plano
                        if (!SancarlinaApp.isAppInForeground) {
                            val prefs = context.getSharedPreferences("update_prefs", Context.MODE_PRIVATE)
                            val lastNotified = prefs.getInt("last_notified_version", -1)
                            
                            if (latestVersionCode != lastNotified) {
                                showUpdateNotification(releaseNotes)
                                prefs.edit().putInt("last_notified_version", latestVersionCode).apply()
                            }
                        }

                        // El callback para el modal interno se dispara siempre para que MainActivity decida
                        withContext(Dispatchers.Main) {
                            onUpdateAvailable(apkUrl, releaseNotes)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showUpdateNotification(notes: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("¡Sancarlina tiene mejoras!")
            .setContentText("Hay una nueva actualización lista para instalar.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(notes))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_upload, "INSTALAR AHORA", pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1001, notification)
    }

    suspend fun downloadAndInstallWithProgress(
        apkUrl: String,
        onProgress: (Float) -> Unit,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(apkUrl).build()
                val response = client.newCall(request).execute()
                
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) { onError("Error en el servidor") }
                    return@withContext
                }

                val body = response.body ?: throw Exception("Cuerpo de respuesta vacío")
                val totalBytes = body.contentLength()
                val fileName = "sancarlina_update.apk"
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
                
                body.byteStream().use { input ->
                    FileOutputStream(file).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var downloadedBytes = 0L
                        
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloadedBytes += bytesRead
                            if (totalBytes > 0) {
                                val progress = downloadedBytes.toFloat() / totalBytes.toFloat()
                                withContext(Dispatchers.Main) { onProgress(progress) }
                            }
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    onComplete()
                    installApk(file)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e.message ?: "Error desconocido") }
            }
        }
    }

    private fun installApk(file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        context.startActivity(intent)
    }
}
