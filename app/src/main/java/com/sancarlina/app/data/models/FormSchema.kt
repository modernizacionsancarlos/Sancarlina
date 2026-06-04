package com.sancarlina.app.data.models

import com.google.firebase.firestore.PropertyName

data class FormSchema(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    @get:PropertyName("tenantId") @set:PropertyName("tenantId")
    var tenantId: String = "",
    @get:PropertyName("tenant_id") @set:PropertyName("tenant_id")
    var tenant_id: String = "",
    @get:PropertyName("submit_url") @set:PropertyName("submit_url")
    var submitUrl: String? = null,
    @get:PropertyName("is_public") @set:PropertyName("is_public")
    var isPublic: Boolean = false,
    @get:PropertyName("accepts_responses") @set:PropertyName("accepts_responses")
    var acceptsResponses: Boolean = false,
    val status: String = "draft"
)
