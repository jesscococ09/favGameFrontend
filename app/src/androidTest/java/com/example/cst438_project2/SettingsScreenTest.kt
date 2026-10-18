package com.example.cst438_project2.ui.screens

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.cst438_project2.data.model.User
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
                text = "Users (0)",
                substring = false
            )
            .assertCountEquals(0)

        composeTestRule
            .onAllNodesWithText(
                text = "View User",
                substring = false
            )
            .assertCountEquals(0)

        composeTestRule
            .onAllNodesWithText(
                text = "Make Admin",
                substring = false
            )
            .assertCountEquals(0)

        composeTestRule
            .onAllNodesWithText(
                text = "Make User",
                substring = false
            )
            .assertCountEquals(0)
    }

    @Test
    fun admin_seesAdminUserList() {
        val otherUser = User(
            id = "2",
            githubId = "456",
            displayName = "Other User",
            iconUrl = "",
            isAdmin = false
        )

        composeTestRule.setContent {
            SettingsScreen(
                onBackClick = {},
                onLogoutClick = {},
                isAdmin = true,
                currentUserId = "1",
                adminUsers = listOf(otherUser)
            )
        }

        composeTestRule
            .onAllNodesWithText(
                text = "Users (1)",
                substring = false
            )[0]
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText(
                text = "Other User",
                substring = false
            )
            .assertCountEquals(1)

        composeTestRule
            .onAllNodesWithText(
                text = "View User",
                substring = false
            )
            .assertCountEquals(1)

        composeTestRule
            .onAllNodesWithText(
                text = "Make Admin",
                substring = false
            )
            .assertCountEquals(1)

        composeTestRule
            .onAllNodesWithText(
                text = "Delete",
                substring = false
            )
            .assertCountEquals(1)
    }

    @Test
    fun selectedUser_informationIsAvailable() {
        val selectedUser = User(
            id = "2",
            githubId = "456",
            displayName = "Selected User Name",
            iconUrl = "",
            isAdmin = false
        )

        composeTestRule.setContent {
            SettingsScreen(
                onBackClick = {},
                onLogoutClick = {},
                isAdmin = true,
                selectedAdminUser = selectedUser
            )
        }

        composeTestRule
            .onAllNodesWithText(
                text = "Selected User",
                substring = false
            )
            .assertCountEquals(1)

        composeTestRule
            .onAllNodesWithText(
                text = "Selected User Name",
                substring = false
            )
            .assertCountEquals(1)

        composeTestRule
            .onAllNodesWithText(
                text = "User ID: 2",
                substring = false
            )
            .assertCountEquals(1)

        composeTestRule
            .onAllNodesWithText(
                text = "GitHub ID: 456",
                substring = false
            )
            .assertCountEquals(1)

        composeTestRule
            .onAllNodesWithText(
                text = "Role: User",
                substring = false
            )
            .assertCountEquals(1)
    }

    @Test
    fun currentAdmin_cannotManageOwnAccountFromUserList() {
        val currentAdmin = User(
            id = "1",
            githubId = "123",
            displayName = "Current Admin",
            iconUrl = "",
            isAdmin = true
        )

        composeTestRule.setContent {
            SettingsScreen(
                onBackClick = {},
                onLogoutClick = {},
                isAdmin = true,
                currentUserId = "1",
                adminUsers = listOf(currentAdmin)
            )
        }

        composeTestRule
            .onAllNodesWithText(
                text = "Current Admin",
                substring = false
            )
            .assertCountEquals(1)

        composeTestRule
            .onAllNodesWithText(
                text = "Current account",
                substring = false
            )
            .assertCountEquals(1)

        composeTestRule
            .onAllNodesWithText(
                text = "Make User",
                substring = false
            )
            .assertCountEquals(0)

        composeTestRule
            .onAllNodesWithText(
                text = "Make Admin",
                substring = false
            )
            .assertCountEquals(0)

        composeTestRule
            .onAllNodesWithText(
                text = "Delete",
                substring = false
            )
            .assertCountEquals(0)
    }

    @Test
    fun adminUserCount_matchesListSize() {
        val currentAdmin = User(
            id = "1",
            githubId = "123",
            displayName = "Current Admin",
            iconUrl = "",
            isAdmin = true
        )

        val otherUser = User(
            id = "2",
            githubId = "456",
            displayName = "Other User",
            iconUrl = "",
            isAdmin = false
        )

        composeTestRule.setContent {
            SettingsScreen(
                onBackClick = {},
                onLogoutClick = {},
                isAdmin = true,
                currentUserId = "1",
                adminUsers = listOf(
                    currentAdmin,
                    otherUser
                )
            )
        }

        composeTestRule
            .onAllNodesWithText(
                text = "Users (2)",
                substring = false
            )
            .assertCountEquals(1)
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
            .onAllNodesWithText(
                text = "Log Out",
                substring = false
            )[0]
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()

        composeTestRule.runOnIdle {
            assertTrue(logoutCalled)
        }
    }
}