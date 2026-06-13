package com.sancarlina.app.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sancarlina.app.ui.features.common.OfflineContent
import com.sancarlina.app.ui.theme.SancarlinaTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflineContentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun offlineContent_showsMessageAndRetryCallback() {
        var retryCalled = false

        composeTestRule.setContent {
            SancarlinaTheme {
                OfflineContent(onRetry = { retryCalled = true })
            }
        }

        composeTestRule.onNodeWithText("Sin conexión").assertExists()
        composeTestRule.onNodeWithTag("offline_retry").performClick()
        assertTrue(retryCalled)
    }
}
