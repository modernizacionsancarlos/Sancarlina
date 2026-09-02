package com.sancarlina.app.data.local

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sancarlina.app.data.models.FormField
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.models.OfflineAttachment
import com.sancarlina.app.data.models.OfflineSubmission
import com.sancarlina.app.data.models.SubmissionSyncStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class OfflineFormsStoreTest {
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun schemaAndMultipleSubmissionsPersistWithAllSyncStates() {
        val store = OfflineFormsStore(context)
        val suffix = UUID.randomUUID().toString()
        val schema = FormSchema(
            id = "form-$suffix",
            title = "Formulario offline",
            tenantId = "tenant-$suffix",
            tenant_id = "tenant-$suffix",
            allowedRoles = listOf("registrar"),
            assignedUserIds = listOf("user"),
            fieldRegistrationEnabled = true,
            fields = listOf(FormField(id = "name", label = "Nombre", required = true))
        )
        store.cacheSchema(schema)

        assertEquals(schema, store.getCachedSchema(schema.id))
        assertEquals(schema.id, store.getCachedSchemasByTenant(schema.tenantId).first().id)

        val first = submission("submission-a-$suffix", schema)
        val second = submission("submission-b-$suffix", schema)
        val attachment = OfflineAttachment(
            id = "attachment-$suffix",
            submissionId = first.localId,
            fieldId = "photo",
            displayName = "photo.jpg",
            mimeType = "image/jpeg",
            localPath = context.filesDir.resolve("test-photo-$suffix.jpg").absolutePath,
            storagePath = "submissions/user/${first.localId}/photo/0_photo.jpg",
            position = 0,
            remoteUrl = null,
            lastError = null
        )

        store.insertSubmission(first, listOf(attachment))
        store.insertSubmission(second, emptyList())
        assertNotNull(store.getSubmission(first.localId))
        assertEquals(2, store.getSyncableSubmissions().count { it.localId == first.localId || it.localId == second.localId })
        assertEquals(1, store.getAttachments(first.localId).size)

        store.markSending(first.localId)
        assertEquals(SubmissionSyncStatus.SENDING, store.getSubmission(first.localId)?.status)
        store.markError(first.localId, "sin conexión")
        assertEquals(SubmissionSyncStatus.ERROR, store.getSubmission(first.localId)?.status)
        store.markPending(first.localId)
        assertEquals(SubmissionSyncStatus.PENDING, store.getSubmission(first.localId)?.status)
        store.updateAttachmentRemoteUrl(attachment.id, "https://example.invalid/photo.jpg")
        assertEquals("https://example.invalid/photo.jpg", store.getAttachments(first.localId).single().remoteUrl)
        store.markSent(first.localId, first.localId)
        assertEquals(SubmissionSyncStatus.SENT, store.getSubmission(first.localId)?.status)

        val removedPaths = store.updateSubmissionContent(
            localId = first.localId,
            data = mapOf("name" to "Persona corregida"),
            replacementAttachments = emptyList(),
            replacedAttachmentFields = setOf("photo")
        )
        assertEquals(listOf(attachment.localPath), removedPaths)
        assertEquals("Persona corregida", store.getSubmission(first.localId)?.data?.get("name"))
        assertEquals(SubmissionSyncStatus.PENDING, store.getSubmission(first.localId)?.status)
        assertEquals(0, store.getAttachments(first.localId).size)

        assertEquals(SubmissionSyncStatus.PENDING, store.getSubmission(second.localId)?.status)
        store.close()
    }

    private fun submission(id: String, schema: FormSchema): OfflineSubmission {
        val now = System.currentTimeMillis()
        return OfflineSubmission(
            localId = id,
            formId = schema.id,
            formTitle = schema.title,
            userId = "user",
            data = mapOf("name" to "Persona", "accepted" to true),
            status = SubmissionSyncStatus.PENDING,
            createdAt = now,
            updatedAt = now,
            attemptCount = 0,
            lastError = null,
            remoteId = null
        )
    }
}
