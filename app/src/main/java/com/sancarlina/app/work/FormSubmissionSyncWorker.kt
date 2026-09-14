package com.sancarlina.app.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
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

    /**
     * Requerido para el trabajo acelerado en Android 11 y anteriores, donde el
     * sistema lo ejecuta como servicio en primer plano.
     */
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    SYNC_CHANNEL_ID,
                    "Envío de formularios",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Avisa mientras los relevamientos guardados se envían al municipio."
                }
            )
        }
        val notification = NotificationCompat.Builder(applicationContext, SYNC_CHANNEL_ID)
            .setContentTitle("Enviando relevamientos")
            .setContentText("Subiendo las respuestas guardadas en el dispositivo.")
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        return ForegroundInfo(SYNC_NOTIFICATION_ID, notification)
    }

    private companion object {
        const val SYNC_CHANNEL_ID = "gondolapp_form_sync"
        const val SYNC_NOTIFICATION_ID = 4711
    }
}

object FormSyncScheduler {
    private val networkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun enqueue(context: Context) {
        // Trabajo acelerado: el Ahorro de datos de Android difiere el trabajo común
        // en segundo plano sobre redes medidas, así que los relevamientos cargados
        // con datos móviles esperaban hasta encontrar Wi-Fi. El trabajo acelerado
        // queda exento de esa restricción. Si no hay cuota disponible, corre como
        // trabajo común en vez de fallar.
        val request = OneTimeWorkRequestBuilder<FormSubmissionSyncWorker>()
            .setConstraints(networkConstraints)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
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
