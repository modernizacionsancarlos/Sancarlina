package com.sancarlina.app.data.models

import com.google.firebase.firestore.PropertyName

data class FormTemplate(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val icon: String = "store",
    val category: String = "comercios",
    @get:PropertyName("template_source") @set:PropertyName("template_source")
    var templateSource: String? = null,
    val schema: FormSchema = FormSchema()
)
