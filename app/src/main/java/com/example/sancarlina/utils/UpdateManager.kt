package com.example.sancarlina.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.sancarlina.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateManager(private val context: Context) {

    private val client = OkHttpClient()
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    // URL de prueba: He puesto una URL de un APK de ejemplo para que veas el flujo.
    // En el futuro, reemplázala por tu propia URL de GitHub o Servidor.
    private val CONFIG_URL = "https://raw.githubusercontent.com/zorro-municipalidad/sancarlina-updates/main/config.json"

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

    fun downloadAndInstall(apkUrl: String) {
        val fileName = "sancarlina_update.apk"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (file.exists()) file.delete()

        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("Sancarlina Actualización")
            .setDescription("Descargando nueva versión...")
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadId = downloadManager.enqueue(request)

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId == id) {
                    installApk(file)
                    context.unregisterReceiver(this)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
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
