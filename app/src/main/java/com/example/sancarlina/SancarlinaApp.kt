package com.example.sancarlina

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.*
import com.example.sancarlina.workers.UpdateWorker
import java.util.concurrent.TimeUnit

class SancarlinaApp : Application() {

    companion object {
        var isAppInForeground: Boolean = false
            private set
    }

    override fun onCreate() {
        super.onCreate()
        
        // Rastreador de estado de la aplicación (Primer plano / Segundo plano)
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                isAppInForeground = true
            }

            override fun onStop(owner: LifecycleOwner) {
                isAppInForeground = false
            }
        })

        scheduleUpdateChecks()
    }

    private fun scheduleUpdateChecks() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Verificamos cada 1 hora para mayor precisión sin matar la batería
        val updateRequest = PeriodicWorkRequestBuilder<UpdateWorker>(
            1, TimeUnit.HOURS 
        )
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "UpdateCheckWork",
            ExistingPeriodicWorkPolicy.KEEP,
            updateRequest
        )
    }
}
