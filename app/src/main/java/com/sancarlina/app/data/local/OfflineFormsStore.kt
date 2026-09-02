package com.sancarlina.app.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.models.OfflineAttachment
import com.sancarlina.app.data.models.OfflineSubmission
import com.sancarlina.app.data.models.SubmissionSyncStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OfflineFormsStore(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    private val lock = Any()
    private val _submissions = MutableStateFlow<List<OfflineSubmission>>(emptyList())
    val submissions: StateFlow<List<OfflineSubmission>> = _submissions.asStateFlow()

    init {
        refreshSubmissions()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE form_schemas (
                form_id TEXT PRIMARY KEY NOT NULL,
                tenant_id TEXT NOT NULL,
                schema_json TEXT NOT NULL,
                cached_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE form_submissions (
                local_id TEXT PRIMARY KEY NOT NULL,
                form_id TEXT NOT NULL,
                form_title TEXT NOT NULL,
                user_id TEXT NOT NULL,
                data_json TEXT NOT NULL,
                sync_status TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                attempt_count INTEGER NOT NULL DEFAULT 0,
                last_error TEXT,
                remote_id TEXT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE submission_attachments (
                attachment_id TEXT PRIMARY KEY NOT NULL,
                submission_id TEXT NOT NULL,
                field_id TEXT NOT NULL,
                display_name TEXT NOT NULL,
                mime_type TEXT NOT NULL,
                local_path TEXT NOT NULL,
                storage_path TEXT NOT NULL,
                position INTEGER NOT NULL,
                remote_url TEXT,
                last_error TEXT,
                FOREIGN KEY(submission_id) REFERENCES form_submissions(local_id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_submissions_status ON form_submissions(sync_status, created_at)")
        db.execSQL("CREATE INDEX idx_attachments_submission ON submission_attachments(submission_id, field_id, position)")
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    fun cacheSchema(schema: FormSchema) = synchronized(lock) {
        val tenantId = schema.tenantId.ifBlank { schema.tenant_id }
        writableDatabase.insertWithOnConflict(
            "form_schemas",
            null,
            ContentValues().apply {
                put("form_id", schema.id)
                put("tenant_id", tenantId)
                put("schema_json", FormJsonCodec.encodeSchema(schema))
                put("cached_at", System.currentTimeMillis())
            },
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun getCachedSchema(formId: String): FormSchema? = synchronized(lock) {
        readableDatabase.query(
            "form_schemas",
            arrayOf("schema_json"),
            "form_id = ?",
            arrayOf(formId),
            null,
            null,
            null,
            "1"
        ).use { cursor ->
            if (cursor.moveToFirst()) decodeSchemaOrNull(cursor.getString(0)) else null
        }
    }

    fun getCachedSchemasByTenant(tenantId: String): List<FormSchema> = synchronized(lock) {
        readableDatabase.query(
            "form_schemas",
            arrayOf("schema_json"),
            "tenant_id = ?",
            arrayOf(tenantId),
            null,
            null,
            "cached_at DESC"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) decodeSchemaOrNull(cursor.getString(0))?.let(::add)
            }
        }
    }

    fun getAllCachedSchemas(): List<FormSchema> = synchronized(lock) {
        readableDatabase.query(
            "form_schemas",
            arrayOf("schema_json"),
            null,
            null,
            null,
            null,
            "cached_at DESC"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) decodeSchemaOrNull(cursor.getString(0))?.let(::add)
            }
        }
    }

    fun insertSubmission(submission: OfflineSubmission, attachments: List<OfflineAttachment>) = synchronized(lock) {
        writableDatabase.beginTransaction()
        try {
            writableDatabase.insertOrThrow("form_submissions", null, submission.toContentValues())
            attachments.forEach { attachment ->
                writableDatabase.insertOrThrow("submission_attachments", null, attachment.toContentValues())
            }
            writableDatabase.setTransactionSuccessful()
        } finally {
            writableDatabase.endTransaction()
        }
        refreshSubmissionsLocked()
    }

    fun updateSubmissionContent(
        localId: String,
        data: Map<String, Any?>,
        replacementAttachments: List<OfflineAttachment>,
        replacedAttachmentFields: Set<String>
    ): List<String> = synchronized(lock) {
        val previousPaths = if (replacedAttachmentFields.isEmpty()) {
            emptyList()
        } else {
            val placeholders = replacedAttachmentFields.joinToString(",") { "?" }
            readableDatabase.query(
                "submission_attachments",
                arrayOf("local_path"),
                "submission_id = ? AND field_id IN ($placeholders)",
                arrayOf(localId, *replacedAttachmentFields.toTypedArray()),
                null,
                null,
                null
            ).use { cursor -> buildList { while (cursor.moveToNext()) add(cursor.getString(0)) } }
        }

        writableDatabase.beginTransaction()
        try {
            writableDatabase.update(
                "form_submissions",
                ContentValues().apply {
                    put("data_json", FormJsonCodec.encodeValues(data))
                    put("sync_status", SubmissionSyncStatus.PENDING.name)
                    put("updated_at", System.currentTimeMillis())
                    putNull("last_error")
                },
                "local_id = ?",
                arrayOf(localId)
            )
            if (replacedAttachmentFields.isNotEmpty()) {
                val placeholders = replacedAttachmentFields.joinToString(",") { "?" }
                writableDatabase.delete(
                    "submission_attachments",
                    "submission_id = ? AND field_id IN ($placeholders)",
                    arrayOf(localId, *replacedAttachmentFields.toTypedArray())
                )
            }
            replacementAttachments.forEach { attachment ->
                writableDatabase.insertOrThrow(
                    "submission_attachments",
                    null,
                    attachment.toContentValues()
                )
            }
            writableDatabase.setTransactionSuccessful()
        } finally {
            writableDatabase.endTransaction()
        }
        refreshSubmissionsLocked()
        previousPaths
    }

    fun getSubmission(localId: String): OfflineSubmission? = synchronized(lock) {
        readableDatabase.query(
            "form_submissions",
            null,
            "local_id = ?",
            arrayOf(localId),
            null,
            null,
            null,
            "1"
        ).use { cursor -> if (cursor.moveToFirst()) cursor.toSubmission() else null }
    }

    fun getSyncableSubmissions(): List<OfflineSubmission> = synchronized(lock) {
        readableDatabase.query(
            "form_submissions",
            null,
            "sync_status IN (?, ?, ?)",
            arrayOf(
                SubmissionSyncStatus.PENDING.name,
                SubmissionSyncStatus.ERROR.name,
                SubmissionSyncStatus.SENDING.name
            ),
            null,
            null,
            "created_at ASC"
        ).use { cursor -> buildList { while (cursor.moveToNext()) add(cursor.toSubmission()) } }
    }

    fun getAttachments(submissionId: String): List<OfflineAttachment> = synchronized(lock) {
        readableDatabase.query(
            "submission_attachments",
            null,
            "submission_id = ?",
            arrayOf(submissionId),
            null,
            null,
            "field_id ASC, position ASC"
        ).use { cursor -> buildList { while (cursor.moveToNext()) add(cursor.toAttachment()) } }
    }

    fun markSending(localId: String) = updateSubmission(
        localId = localId,
        values = ContentValues().apply {
            put("sync_status", SubmissionSyncStatus.SENDING.name)
            putNull("last_error")
            put("attempt_count", (getSubmission(localId)?.attemptCount ?: 0) + 1)
        }
    )

    fun markPending(localId: String) = updateSubmission(
        localId,
        ContentValues().apply {
            put("sync_status", SubmissionSyncStatus.PENDING.name)
            putNull("last_error")
        }
    )

    fun markError(localId: String, message: String) = updateSubmission(
        localId,
        ContentValues().apply {
            put("sync_status", SubmissionSyncStatus.ERROR.name)
            put("last_error", message.take(MAX_ERROR_LENGTH))
        }
    )

    fun markSent(localId: String, remoteId: String) = updateSubmission(
        localId,
        ContentValues().apply {
            put("sync_status", SubmissionSyncStatus.SENT.name)
            put("remote_id", remoteId)
            putNull("last_error")
        }
    )

    fun updateAttachmentRemoteUrl(attachmentId: String, remoteUrl: String) = synchronized(lock) {
        writableDatabase.update(
            "submission_attachments",
            ContentValues().apply {
                put("remote_url", remoteUrl)
                putNull("last_error")
            },
            "attachment_id = ?",
            arrayOf(attachmentId)
        )
    }

    fun updateAttachmentError(attachmentId: String, error: String) = synchronized(lock) {
        writableDatabase.update(
            "submission_attachments",
            ContentValues().apply { put("last_error", error.take(MAX_ERROR_LENGTH)) },
            "attachment_id = ?",
            arrayOf(attachmentId)
        )
    }

    private fun updateSubmission(localId: String, values: ContentValues) = synchronized(lock) {
        writableDatabase.update("form_submissions", values, "local_id = ?", arrayOf(localId))
        refreshSubmissionsLocked()
    }

    private fun refreshSubmissions() = synchronized(lock) { refreshSubmissionsLocked() }

    private fun refreshSubmissionsLocked() {
        _submissions.value = readableDatabase.query(
            "form_submissions",
            null,
            null,
            null,
            null,
            null,
            "created_at DESC"
        ).use { cursor -> buildList { while (cursor.moveToNext()) add(cursor.toSubmission()) } }
    }

    private fun OfflineSubmission.toContentValues() = ContentValues().apply {
        put("local_id", localId)
        put("form_id", formId)
        put("form_title", formTitle)
        put("user_id", userId)
        put("data_json", FormJsonCodec.encodeValues(data))
        put("sync_status", status.name)
        put("created_at", createdAt)
        put("updated_at", updatedAt)
        put("attempt_count", attemptCount)
        put("last_error", lastError)
        put("remote_id", remoteId)
    }

    private fun OfflineAttachment.toContentValues() = ContentValues().apply {
        put("attachment_id", id)
        put("submission_id", submissionId)
        put("field_id", fieldId)
        put("display_name", displayName)
        put("mime_type", mimeType)
        put("local_path", localPath)
        put("storage_path", storagePath)
        put("position", position)
        put("remote_url", remoteUrl)
        put("last_error", lastError)
    }

    private fun Cursor.toSubmission() = OfflineSubmission(
        localId = string("local_id"),
        formId = string("form_id"),
        formTitle = string("form_title"),
        userId = string("user_id"),
        data = FormJsonCodec.decodeValues(string("data_json")),
        status = runCatching { SubmissionSyncStatus.valueOf(string("sync_status")) }
            .getOrDefault(SubmissionSyncStatus.ERROR),
        createdAt = long("created_at"),
        updatedAt = long("updated_at"),
        attemptCount = int("attempt_count"),
        lastError = nullableString("last_error"),
        remoteId = nullableString("remote_id")
    )

    private fun Cursor.toAttachment() = OfflineAttachment(
        id = string("attachment_id"),
        submissionId = string("submission_id"),
        fieldId = string("field_id"),
        displayName = string("display_name"),
        mimeType = string("mime_type"),
        localPath = string("local_path"),
        storagePath = string("storage_path"),
        position = int("position"),
        remoteUrl = nullableString("remote_url"),
        lastError = nullableString("last_error")
    )

    private fun Cursor.string(column: String) = getString(getColumnIndexOrThrow(column))
    private fun Cursor.nullableString(column: String): String? =
        getColumnIndexOrThrow(column).let { index -> if (isNull(index)) null else getString(index) }
    private fun Cursor.long(column: String) = getLong(getColumnIndexOrThrow(column))
    private fun Cursor.int(column: String) = getInt(getColumnIndexOrThrow(column))

    private fun decodeSchemaOrNull(json: String): FormSchema? = runCatching {
        FormJsonCodec.decodeSchema(json)
    }.getOrNull()

    companion object {
        const val DATABASE_NAME = "offline_forms.db"
        private const val DATABASE_VERSION = 1
        private const val MAX_ERROR_LENGTH = 500
    }
}
