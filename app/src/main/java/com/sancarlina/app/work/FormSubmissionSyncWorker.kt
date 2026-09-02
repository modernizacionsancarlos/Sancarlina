package com.sancarlina.app.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.sancarlina.app.SancarlinaApp
import java.util.concurrent.TimeUnit

class FormSubmissionSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val app = applicationContext as? SancarlinaApp ?: return Result.failure()
        val summary = app.container.offlineSubmissionsRepository.syncPending()
        return if (summary.hasTransientFailures) Result.retry() else Result.success()
    }
}

object FormSyncScheduler {
    private val networkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun enqueue(context: Context) {
        val request = OneTimeWorkRequestBuilder<FormSubmissionSyncWorker>()
            .setConstraints(networkConstraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    fun ensurePeriodicSync(context: Context) {
        val request = PeriodicWorkRequestBuilder<FormSubmissionSyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(networkConstraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private const val UNIQUE_WORK_NAME = "offline-form-submission-sync"
    private const val PERIODIC_WORK_NAME = "offline-form-submission-periodic-sync"
}
