package com.sancarlina.app.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sancarlina.app.ui.features.points.BenefitsListContent
import com.sancarlina.app.ui.theme.SancarlinaTheme
import com.sancarlina.app.viewmodel.BenefitItem
import com.sancarlina.app.viewmodel.PointsUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BenefitsContentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun benefitsContent_rendersAllSectionsWithoutDuplicateLazyListKeys() {
        composeTestRule.setContent {
            SancarlinaTheme {
                BenefitsListContent(
                    uiState = PointsUiState(
                        benefits = listOf(
                            BenefitItem(
                                id = "points_screen_header",
                                title = "Beneficio de prueba",
                                brand = "Comercio local",
                                cost = 100,
                                category = "Prueba"
                            )
                        )
                    ),
                    onNavigateToScanner = {},
                    onBenefitClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Puntos y Beneficios").assertExists()
        composeTestRule.onNodeWithTag("points_benefits_section").performScrollTo().assertExists()
        composeTestRule.onNodeWithText("Beneficio de prueba").performScrollTo().assertExists()
    }
}
