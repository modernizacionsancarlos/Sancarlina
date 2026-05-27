package com.example.sancarlina

import android.app.Application
import androidx.work.*
import com.example.sancarlina.workers.UpdateWorker
import java.util.concurrent.TimeUnit

class SancarlinaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        scheduleUpdateChecks()
    }

    private fun scheduleUpdateChecks() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val updateRequest = PeriodicWorkRequestBuilder<UpdateWorker>(
            2, TimeUnit.HOURS // Check every 2 hours
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "UpdateCheckWork",
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing work to avoid resets
            updateRequest
        )
    }
}
