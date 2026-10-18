package com.example.cst438_project2.ui.screens

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.cst438_project2.data.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

        composeTestRule
            .onAllNodesWithText(
                text = "Current account",
                substring = false
            )
            .assertCountEquals(0)
    }

    @Test
    fun admin_seesAdminFeatures() {
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
            .onNodeWithText("Users (1)")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Other User")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("GitHub ID: 456")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText(
                    text = "Make Admin",
                    substring = false
                ) and hasClickAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText(
                    text = "Delete",
                    substring = false
                ) and hasClickAction()
            )
            .assertIsDisplayed()
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
            .onNodeWithText("Current Admin")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Current account")
            .assertIsDisplayed()

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
    fun otherAdmin_canBeChangedToUser() {
        val otherAdmin = User(
            id = "2",
            githubId = "456",
            displayName = "Other Admin",
            iconUrl = "",
            isAdmin = true
        )

        composeTestRule.setContent {
            SettingsScreen(
                onBackClick = {},
                onLogoutClick = {},
                isAdmin = true,
                currentUserId = "1",
                adminUsers = listOf(otherAdmin)
            )
        }

        composeTestRule
            .onNodeWithText("Other Admin")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText(
                    text = "Make User",
                    substring = false
                ) and hasClickAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText(
                    text = "Delete",
                    substring = false
                ) and hasClickAction()
            )
            .assertIsDisplayed()
    }

    @Test
    fun makeAdmin_showsConfirmationBeforeCallback() {
        var callbackCalled = false

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
                adminUsers = listOf(otherUser),
                onToggleAdminUser = { _, _ ->
                    callbackCalled = true
                }
            )
        }

        composeTestRule
            .onNode(
                hasText(
                    text = "Make Admin",
                    substring = false
                ) and hasClickAction()
            )
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithText("Make User Admin?")
            .assertIsDisplayed()

        assertFalse(callbackCalled)
    }

    @Test
    fun makeAdmin_confirmInvokesCallback() {
        var callbackCalled = false
        var callbackUserId = ""
        var callbackAdminValue = false

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
                adminUsers = listOf(otherUser),
                onToggleAdminUser = { userId, newAdminValue ->
                    callbackCalled = true
                    callbackUserId = userId
                    callbackAdminValue = newAdminValue
                }
            )
        }

        composeTestRule
            .onNode(
                hasText(
                    text = "Make Admin",
                    substring = false
                ) and hasClickAction()
            )
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithText("Make User Admin?")
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText(
                text = "Make Admin",
                substring = false
            )[1]
            .performClick()

        composeTestRule.runOnIdle {
            assertTrue(callbackCalled)
            assertEquals("2", callbackUserId)
            assertTrue(callbackAdminValue)
        }
    }

    @Test
    fun makeUser_confirmInvokesCallbackWithFalse() {
        var callbackCalled = false
        var callbackUserId = ""
        var callbackAdminValue = true

        val otherAdmin = User(
            id = "2",
            githubId = "456",
            displayName = "Other Admin",
            iconUrl = "",
            isAdmin = true
        )

        composeTestRule.setContent {
            SettingsScreen(
                onBackClick = {},
                onLogoutClick = {},
                isAdmin = true,
                currentUserId = "1",
                adminUsers = listOf(otherAdmin),
                onToggleAdminUser = { userId, newAdminValue ->
                    callbackCalled = true
                    callbackUserId = userId
                    callbackAdminValue = newAdminValue
                }
            )
        }

        composeTestRule
            .onNode(
                hasText(
                    text = "Make User",
                    substring = false
                ) and hasClickAction()
            )
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithText("Make Admin a User?")
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText(
                text = "Make User",
                substring = false
            )[1]
            .performClick()

        composeTestRule.runOnIdle {
            assertTrue(callbackCalled)
            assertEquals("2", callbackUserId)
            assertFalse(callbackAdminValue)
        }
    }

    @Test
    fun deleteUser_showsConfirmationBeforeCallback() {
        var callbackCalled = false

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
                adminUsers = listOf(otherUser),
                onDeleteAdminUser = {
                    callbackCalled = true
                }
            )
        }

        composeTestRule
            .onNode(
                hasText(
                    text = "Delete",
                    substring = false
                ) and hasClickAction()
            )
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithText("Delete User?")
            .assertIsDisplayed()

        assertFalse(callbackCalled)
    }

    @Test
    fun deleteUser_confirmInvokesCallback() {
        var callbackCalled = false
        var callbackUserId = ""

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
                adminUsers = listOf(otherUser),
                onDeleteAdminUser = { userId ->
                    callbackCalled = true
                    callbackUserId = userId
                }
            )
        }

        composeTestRule
            .onNode(
                hasText(
                    text = "Delete",
                    substring = false
                ) and hasClickAction()
            )
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithText("Delete User?")
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText(
                text = "Delete",
                substring = false
            )[1]
            .performClick()

        composeTestRule.runOnIdle {
            assertTrue(callbackCalled)
            assertEquals("2", callbackUserId)
        }
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
            .onNodeWithText("Users (2)")
            .performScrollTo()
            .assertIsDisplayed()
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