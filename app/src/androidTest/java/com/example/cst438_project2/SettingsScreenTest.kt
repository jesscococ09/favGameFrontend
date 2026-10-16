package com.example.cst438_project2.ui.screens

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
            .onAllNodesWithText(
                text = "Delete User Comment",
                substring = false
            )
            .assertCountEquals(0)

        composeTestRule
            .onAllNodesWithText(
                text = "Delete User",
                substring = false
            )
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
            .onNode(
                hasText(
                    text = "Delete User Comment",
                    substring = false
                ) and hasClickAction()
            )
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText(
                    text = "Delete User",
                    substring = false
                ) and hasClickAction()
            )
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
            .onNode(
                hasText(
                    text = "Delete User Comment",
                    substring = false
                ) and isNotEnabled()
            )
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsNotEnabled()

        composeTestRule
            .onNode(
                hasText(
                    text = "Delete User",
                    substring = false
                ) and isNotEnabled()
            )
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun logoutButton_invokesCallback() {
        var logoutCalled = false

        composeTestRule.setContent {
            SettingsScreen(
                onBackClick = {},
                onLogoutClick = {
                    logoutCalled = true
                },
                isAdmin = false
            )
        }

        composeTestRule
            .onNode(
                hasText(
                    text = "Log Out",
                    substring = false
                ) and hasClickAction()
            )
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()

        assertTrue(logoutCalled)
    }
}