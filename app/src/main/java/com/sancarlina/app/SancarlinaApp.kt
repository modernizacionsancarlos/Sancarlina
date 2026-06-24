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

class SancarlinaApp : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // 25% de la memoria ram disponible para caché de imágenes
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02) // 2% del almacenamiento para caché de disco
                    .build()
            }
            .crossfade(true) // Animación suave de transición por defecto al cargar imágenes
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
            container = AppContainer()
            android.util.Log.i("GondolApp", "SancarlinaApp: Contenedor de dependencias inicializado.")
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
