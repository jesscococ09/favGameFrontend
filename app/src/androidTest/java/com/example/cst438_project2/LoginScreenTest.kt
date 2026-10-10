package com.example.cst438_project2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.cst438_project2.ui.screens.LoginScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// Composable tests only atm, once api is connected, meaningful tests will be added
class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysTitle() {
        composeTestRule.setContent {
            LoginScreen(onLoginClick = {})
        }
        composeTestRule
            .onNodeWithText("FAVGAME")
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_displaysSubtitle() {
        composeTestRule.setContent {
            LoginScreen(onLoginClick = {})
        }
        composeTestRule
            .onNodeWithText("YOUR GAMES. YOUR LIBRARY.")
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_displaysLoginMessage() {
        composeTestRule.setContent {
            LoginScreen(onLoginClick = {})
        }
        composeTestRule
            .onNodeWithText(
                "Sign in with GitHub to access your game library."
            )
            .assertIsDisplayed()
    }

    @Test
    fun loginButton_isDisplayed() {
        composeTestRule.setContent {
            LoginScreen(onLoginClick = {})
        }
        composeTestRule
            .onNodeWithText("Login with GitHub")
            .assertIsDisplayed()
    }

    @Test
    fun loginButton_displaysCorrectText() {
        composeTestRule.setContent {
            LoginScreen(onLoginClick = {})
        }
        composeTestRule
            .onNodeWithText("Login with GitHub")
            .assertTextContains("Login with GitHub")
    }

    @Test
    fun loginButton_callsLoginCallback() {
        var clicked = false
        composeTestRule.setContent {
            LoginScreen(
                onLoginClick = {
                    clicked = true
                }
            )
        }
        composeTestRule
            .onNodeWithText("Login with GitHub")
            .performClick()
        assertTrue(clicked)
    }
}