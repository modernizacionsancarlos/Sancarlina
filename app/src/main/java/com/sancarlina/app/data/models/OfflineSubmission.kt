package com.sancarlina.app.data.models

enum class SubmissionSyncStatus {
    PENDING,
    SENDING,
    SENT,
    ERROR
}

data class OfflineSubmission(
    val localId: String,
    val formId: String,
    val formTitle: String,
    val userId: String,
    val data: Map<String, Any?>,
    val status: SubmissionSyncStatus,
    val createdAt: Long,
    val updatedAt: Long,
    val attemptCount: Int,
    val lastError: String?,
    val remoteId: String?
)

data class OfflineAttachment(
    val id: String,
    val submissionId: String,
    val fieldId: String,
    val displayName: String,
    val mimeType: String,
    val localPath: String,
    val storagePath: String,
    val position: Int,
    val remoteUrl: String?,
    val lastError: String?
)

data class SyncSummary(
    val sent: Int = 0,
    val failed: Int = 0,
    val permanentFailures: Int = 0
) {
    val hasTransientFailures: Boolean get() = failed > permanentFailures
}

data class QueuedSubmissionResult(
    val localId: String,
    val status: SubmissionSyncStatus
)
