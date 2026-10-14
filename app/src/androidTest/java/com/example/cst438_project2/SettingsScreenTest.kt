package com.example.cst438_project2.ui.screens

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun regularUser_doesNotSeeAdminFeatures() {
        composeTestRule.setContent {
            SettingsScreen(
                onBackClick = {},
                onLogoutClick = {},
                isAdmin = false
            )
        }

        composeTestRule
            .onAllNodesWithText("Delete User Comment")
            .assertCountEquals(0)

        composeTestRule
            .onAllNodesWithText("Delete User")
            .assertCountEquals(0)
    }

    @Test
    fun admin_seesAdminFeatures() {
        composeTestRule.setContent {
            SettingsScreen(
                onBackClick = {},
                onLogoutClick = {},
                isAdmin = true
            )
        }

        composeTestRule
            .onNode(hasText("Delete User Comment") and hasClickAction())
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNode(hasText("Delete User") and hasClickAction())
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun adminDeleteButtons_startDisabled() {
        composeTestRule.setContent {
            SettingsScreen(
                onBackClick = {},
                onLogoutClick = {},
                isAdmin = true
            )
        }

        composeTestRule
            .onNode(hasText("Delete User Comment") and hasClickAction())
            .assertIsNotEnabled()

        composeTestRule
            .onNode(hasText("Delete User") and hasClickAction())
            .assertIsNotEnabled()
    }

    @Test
    fun logoutButton_invokesCallback() {
        var logoutCalled = false

        composeTestRule.setContent {
            SettingsScreen(
                onBackClick = {},
                onLogoutClick = { logoutCalled = true },
                isAdmin = false
            )
        }

        composeTestRule
            .onNodeWithText("Log Out")
            .performClick()

        assertTrue(logoutCalled)
    }
}
