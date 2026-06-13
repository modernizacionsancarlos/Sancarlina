package com.sancarlina.app.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sancarlina.app.ui.features.points.QrScannerContent
import com.sancarlina.app.ui.theme.SancarlinaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QrScannerPermissionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun qrScanner_showsPermissionPromptWhenForcedNoCamera() {
        composeTestRule.setContent {
            SancarlinaTheme {
                QrScannerContent(
                    onBack = {},
                    onSuccess = {},
                    forceNoCameraPermission = true
                )
            }
        }

        composeTestRule.onNodeWithText(
            "Necesitamos permiso de cámara para escanear el código QR."
        ).assertExists()
        composeTestRule.onNodeWithText("CONCEDER PERMISO").assertExists()
        composeTestRule.onNodeWithTag("qr_grant_permission").assertExists()
    }
}
