package com.sancarlina.app.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sancarlina.app.utils.UpdateManager

class UpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val updateManager = UpdateManager(applicationContext)
        
        // Use a simpler check that only triggers notification if update is found
        updateManager.checkForUpdates { _, _ ->
            // UpdateManager already shows the notification inside checkForUpdates
        }
        
        return Result.success()
    }
}
