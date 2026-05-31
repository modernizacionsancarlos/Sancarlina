package com.example.sancarlina.data.model

import com.google.firebase.firestore.PropertyName

data class FormSchema(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    @get:PropertyName("tenantId") @set:PropertyName("tenantId")
    var tenantId: String = "",
    @get:PropertyName("submitUrl") @set:PropertyName("submitUrl")
    var submitUrl: String? = null,
    val active: Boolean = true
)
