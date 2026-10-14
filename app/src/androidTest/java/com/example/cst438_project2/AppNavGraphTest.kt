package com.example.cst438_project2.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class AppNavGraphTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: NavHostController

    // Launch the real navigation graph with a known login state.
    private fun launchGraph(isLoggedIn: Boolean) {
        composeRule.setContent {
            val controller = rememberNavController()

            // Keep a reference so each test can inspect navigation.
            navController = controller

            AppNavGraph(
                isLoggedIn = isLoggedIn,
                onLoginClick = {}, // No real browser launch in tests.
                navController = controller
            )
        }

        composeRule.waitForIdle()
    }

    @Test
    fun loggedInUser_startsAtGameListRoute() {
        launchGraph(isLoggedIn = true)

        // The game list should be visible immediately.
        composeRule.onNodeWithText("Minecraft")
            .assertExists()

        composeRule.onNodeWithText("Elden Ring")
            .assertExists()
    }

    @Test
    fun loggedOutUser_startsAtLoginRoute() {
        launchGraph(isLoggedIn = false)

        // The login screen should be visible immediately.
        // Replace this text with the actual login-screen text
        // if your LoginScreen uses a different label.
        composeRule.onNodeWithText(
            "Login",
            substring = true
        ).assertExists()
    }

    @Test
    fun clickingGame_opensDetailsWithCorrectId() {
        launchGraph(isLoggedIn = true)

        // Tap the Minecraft game card.
        composeRule.onNodeWithText("Minecraft")
            .performClick()

        composeRule.waitForIdle()

        // Verify that the details placeholder displays the selected game's ID.
        composeRule.onNodeWithText("Game Details — Game ID: 1")
            .assertExists()
    }

    @Test
    fun detailsBackStack_returnsToGameList() {
        // Start the app as a logged-in user.
        launchGraph(isLoggedIn = true)

        // Navigate by tapping a game, just like a real user.
        composeRule.onNodeWithText("Minecraft")
            .performClick()

        composeRule.waitForIdle()

        // Verify the game details placeholder opened.
        composeRule.onNodeWithText(
            "Game Details — Game ID: 1"
        ).assertExists()

        // Use the system Back action to return to the game list.
        composeRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        composeRule.waitForIdle()

        // Verify we returned to the game list.
        composeRule.onNodeWithText("Minecraft")
            .assertExists()
    }

    @Test
    fun profileRoute_displaysProfileScreen() {
        launchGraph(isLoggedIn = true)

        // Tap the Profile button on the Game List screen.
        // Adjust this text if your TopBar uses a different label or icon.
        composeRule.onNodeWithContentDescription("Profile")
            .performClick()

        composeRule.waitForIdle()

        composeRule.onNodeWithText("Profile")
            .assertExists()
    }

    @Test
    fun addGameRoute_displaysAddGameScreen() {
        launchGraph(isLoggedIn = true)

        // Tap the Add button on the Game List screen.
        // Adjust this description to match your actual TopBar.
        composeRule.onNodeWithContentDescription("Add")
            .performClick()

        composeRule.waitForIdle()

        composeRule.onNodeWithText("Add Game")
            .assertExists()
    }
}