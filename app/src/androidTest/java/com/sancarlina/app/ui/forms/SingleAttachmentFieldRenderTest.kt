package com.sancarlina.app.ui.forms

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sancarlina.app.data.models.FormField
import com.sancarlina.app.ui.features.forms.PublicFormUiState
import com.sancarlina.app.ui.features.forms.RenderFormField
import com.sancarlina.app.ui.theme.SancarlinaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SingleAttachmentFieldRenderTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun singleImageFieldRendersWithoutCrashing() {
        composeTestRule.setContent {
            SancarlinaTheme {
                RenderFormField(
                    field = FormField(
                        id = "photo",
                        type = "image",
                        label = "Foto del comercio",
                        maxImages = 1,
                        allowMultiple = false
                    ),
                    value = null,
                    selectedImages = emptyList(),
                    existingAttachmentCount = 0,
                    uiState = PublicFormUiState(),
                    onValueChange = {},
                    onImagesChange = {},
                    onClearExistingAttachments = {},
                    onGpsPinChange = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Foto del comercio").assertExists()
        composeTestRule.onNodeWithText("Seleccionar foto (0/1)").assertExists()
    }

    @Test
    fun everyAttachmentConfigurationRendersWithoutCrashing() {
        val fields = listOf(
            FormField(id = "image-zero", type = "image", label = "Imagen límite cero", maxImages = 0),
            FormField(id = "image-one", type = "image", label = "Imagen única", maxImages = 1),
            FormField(
                id = "image-conflict",
                type = "image",
                label = "Imagen configuración contradictoria",
                maxImages = 1,
                allowMultiple = true
            ),
            FormField(id = "image-many", type = "image", label = "Imágenes múltiples", maxImages = 5),
            FormField(id = "file-one", type = "file", label = "Archivo único", maxImages = 1),
            FormField(id = "attachment-one", type = "attachment", label = "Adjunto único", maxImages = 1),
            FormField(id = "image-huge", type = "image", label = "Imagen límite extremo", maxImages = Int.MAX_VALUE)
        )

        composeTestRule.setContent {
            SancarlinaTheme {
                Column {
                    fields.forEach { field ->
                        RenderFormField(
                            field = field,
                            value = null,
                            selectedImages = emptyList(),
                            existingAttachmentCount = 0,
                            uiState = PublicFormUiState(),
                            onValueChange = {},
                            onImagesChange = {},
                            onClearExistingAttachments = {},
                            onGpsPinChange = {}
                        )
                    }
                }
            }
        }

        fields.forEach { field ->
            composeTestRule.onNodeWithText(field.label).assertExists()
        }
    }

    @Test
    fun ordinaryBooleanFieldRendersAsAVisibleControl() {
        composeTestRule.setContent {
            SancarlinaTheme {
                RenderFormField(
                    field = FormField(
                        id = "open_today",
                        type = "boolean",
                        label = "El comercio está abierto hoy",
                        required = true
                    ),
                    value = false,
                    selectedImages = emptyList(),
                    existingAttachmentCount = 0,
                    uiState = PublicFormUiState(),
                    onValueChange = {},
                    onImagesChange = {},
                    onClearExistingAttachments = {},
                    onGpsPinChange = {}
                )
            }
        }

        composeTestRule.onNodeWithText("El comercio está abierto hoy *").assertExists()
    }
}
