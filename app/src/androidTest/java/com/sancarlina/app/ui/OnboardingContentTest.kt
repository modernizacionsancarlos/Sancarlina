package com.sancarlina.app.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sancarlina.app.ui.features.auth.OnboardingContent
import com.sancarlina.app.ui.features.auth.OnboardingDestination
import com.sancarlina.app.ui.theme.SancarlinaTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingContentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun onboarding_guidesThroughAllSteps_andFinishesInHome() {
        var destination: OnboardingDestination? = null
        composeTestRule.setContent {
            SancarlinaTheme {
                OnboardingContent(onFinish = { destination = it })
            }
        }

        composeTestRule.onNodeWithText("Todo San Carlos, en un solo lugar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("onboarding_primary_action").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Encontrá lugares y llegá sin vueltas")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("onboarding_primary_action").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Comprá local y sumá beneficios")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("onboarding_primary_action").performClick()
        composeTestRule.runOnIdle {
            assertEquals(OnboardingDestination.Home, destination)
        }
    }

    @Test
    fun onboarding_skip_isAlwaysAvailable() {
        var destination: OnboardingDestination? = null
        composeTestRule.setContent {
            SancarlinaTheme {
                OnboardingContent(onFinish = { destination = it })
            }
        }

        composeTestRule.onNodeWithText("Omitir").performClick()
        composeTestRule.runOnIdle {
            assertEquals(OnboardingDestination.Home, destination)
        }
    }
}
