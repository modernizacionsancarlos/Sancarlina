package com.sancarlina.app.ui.features.forms

internal data class AttachmentPickerPolicy(
    val selectionLimit: Int,
    val multiple: Boolean
)

internal fun attachmentPickerPolicy(maxItems: Int): AttachmentPickerPolicy {
    val selectionLimit = maxItems.coerceAtLeast(1)
    return AttachmentPickerPolicy(
        selectionLimit = selectionLimit,
        // Un límite de uno siempre usa el selector simple, incluso si el esquema
        // externo combina por error max_images=1 con allow_multiple=true.
        multiple = selectionLimit > 1
    )
}
