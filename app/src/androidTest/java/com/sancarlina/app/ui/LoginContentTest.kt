package com.sancarlina.app.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sancarlina.app.test.FakeAuthViewModel
import com.sancarlina.app.ui.features.auth.LoginContent
import com.sancarlina.app.ui.theme.SancarlinaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginContentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loginContent_rendersWelcomeFieldsAndSubmitButton() {
        val fakeViewModel = FakeAuthViewModel()

        composeTestRule.setContent {
            SancarlinaTheme {
                LoginContent(
                    viewModel = fakeViewModel,
                    onNavigateToRegister = {},
                    onNavigateToForgotPassword = {},
                    onLoginSuccess = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Bienvenido").assertExists()
        composeTestRule.onNodeWithText("tu@email.com").assertExists()
        composeTestRule.onNodeWithText("Ingresar").assertExists()
    }
}
