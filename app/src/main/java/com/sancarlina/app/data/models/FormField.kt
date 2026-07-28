package com.sancarlina.app.data.models

import com.google.firebase.firestore.PropertyName

data class FormField(
    val id: String = "",
    val type: String = "text",
    val label: String = "",
    val required: Boolean = false,
    @get:PropertyName("tenant_mapping") @set:PropertyName("tenant_mapping")
    var tenantMapping: String? = null,
    val options: List<String> = emptyList(),
    val placeholder: String? = null,
    @get:PropertyName("help_text") @set:PropertyName("help_text")
    var helpText: String? = null,
    val description: String? = null,
    @get:PropertyName("max_images") @set:PropertyName("max_images")
    var maxImages: Int = 1,
    @get:PropertyName("allow_multiple") @set:PropertyName("allow_multiple")
    var allowMultiple: Boolean = false
)
