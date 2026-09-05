package com.sancarlina.app

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.sancarlina.app.di.AppContainer
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.sancarlina.app.data.templates.BuiltinFormTemplates
import com.sancarlina.app.work.FormSyncScheduler

class SancarlinaApp : Application(), ImageLoaderFactory {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun newImageLoader(): ImageLoader {
        val okHttpClient = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return ImageLoader.Builder(this)
            .okHttpClient(okHttpClient)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.04)
                    .build()
            }
            .crossfade(true)
            .allowHardware(false) // Fundamental: evita que los hardware bitmaps se rendericen en blanco en emuladores
            .build()
    }

    // Instancia global del contenedor de dependencias
    lateinit var container: AppContainer

    companion object {
        var isAppInForeground: Boolean = false
            private set
    }

    override fun onCreate() {
        super.onCreate()
        android.util.Log.i("GondolApp", "SancarlinaApp: Iniciando aplicación...")
        try {
            container = AppContainer(applicationContext)
            android.util.Log.i("GondolApp", "SancarlinaApp: Contenedor de dependencias inicializado.")
            applicationScope.launch {
                BuiltinFormTemplates.ALL_TEMPLATES.forEach { template ->
                    container.offlineFormsStore.cacheSchema(template.schema)
                }
            }
            FormSyncScheduler.enqueue(this)
            FormSyncScheduler.ensurePeriodicSync(this)
            applicationScope.launch { container.pushPreferencesRepository.initialize() }
            container.auth.addAuthStateListener {
                applicationScope.launch { container.pushPreferencesRepository.registerCurrentToken() }
                FormSyncScheduler.enqueue(this)
            }
        } catch (e: Exception) {
            android.util.Log.e("GondolApp", "SancarlinaApp: ERROR al inicializar AppContainer", e)
        }
        
        // Rastreador de estado de la aplicación (Primer plano / Segundo plano)
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                isAppInForeground = true
            }

            override fun onStop(owner: LifecycleOwner) {
                isAppInForeground = false
            }
        })
    }
}
