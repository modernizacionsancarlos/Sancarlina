package com.sancarlina.app.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sancarlina.app.ui.features.home.HomeContentBody
import com.sancarlina.app.ui.theme.SancarlinaTheme
import com.sancarlina.app.viewmodel.CategoryItem
import com.sancarlina.app.viewmodel.HomeUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeContentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun homeContent_rendersExploreSectionWithFakeState() {
        val fakeState = HomeUiState(
            categories = listOf(CategoryItem(name = "BODEGAS")),
            banners = emptyList()
        )

        composeTestRule.setContent {
            SancarlinaTheme {
                HomeContentBody(
                    uiState = fakeState,
                    onNavigateToCategory = {},
                    onNavigateToSearch = {},
                    onNavigateToNews = {},
                    onNavigateToDetail = {}
                )
            }
        }

        composeTestRule.onAllNodesWithText("Bodegas").assertCountEquals(2)
    }
}
