package com.sancarlina.app

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.sancarlina.app.di.AppContainer

class SancarlinaApp : Application() {

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
