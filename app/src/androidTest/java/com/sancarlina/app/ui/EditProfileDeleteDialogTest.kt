package com.sancarlina.app.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sancarlina.app.ui.features.profile.EditProfileContent
import com.sancarlina.app.ui.features.profile.EditProfileViewModel
import com.sancarlina.app.ui.theme.SancarlinaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditProfileDeleteDialogTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun editProfile_deleteDialogFlow_reachesPasswordStep() {
        val viewModel = EditProfileViewModel()

        composeTestRule.setContent {
            SancarlinaTheme {
                EditProfileContent(
                    viewModel = viewModel,
                    onBack = {},
                    onLogout = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Eliminar mi cuenta")
            .performScrollTo()
            .performClick()

        composeTestRule.onNodeWithText("¿Eliminar cuenta?").assertExists()
        composeTestRule.onNodeWithText("CONTINUAR").performClick()

        composeTestRule.onNodeWithText("Confirmá tu contraseña").assertExists()
    }
}
