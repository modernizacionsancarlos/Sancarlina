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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.sancarlina.app.data.templates.BuiltinFormTemplates
import com.sancarlina.app.work.FormSyncScheduler

class SancarlinaApp : Application(), ImageLoaderFactory {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.30) // 30% de la memoria ram disponible para caché de imágenes
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(150L * 1024 * 1024) // 150 MB dedicados a caché de imágenes
                    .build()
            }
            // Evita una demora visual extra al mostrar una imagen ya disponible en caché.
            .crossfade(false)
            .allowHardware(true)
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
            // Precarga agresiva de datos e imágenes en segundo plano para acceso instantáneo
            applicationScope.launch {
                com.sancarlina.app.data.cache.AppPreloader.preloadAll(applicationContext, container)
            }
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
