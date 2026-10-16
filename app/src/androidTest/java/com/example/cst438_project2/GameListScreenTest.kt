package com.example.cst438_project2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.cst438_project2.ui.screens.Game
import com.example.cst438_project2.ui.screens.GameListScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GameListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testGames = listOf(
        Game(
            id = "1",
            title = "Minecraft",
            imageUrl = "",
            rating = 9
        ),
        Game(
            id = "2",
            title = "Elden Ring",
            imageUrl = "",
            rating = 10
        ),
        Game(
            id = "3",
            title = "Celeste",
            imageUrl = "",
            rating = 8
        )
    )

    @Test
    fun showsGameTitles() {
        composeTestRule.setContent {
            GameListScreen(
                games = testGames,
                displayName = "Test User",
                onGameClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("My Games")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Minecraft")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Elden Ring")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Celeste")
            .assertIsDisplayed()
    }

    @Test
    fun showsWelcomeAddButtonAndProfileCircle() {
        composeTestRule.setContent {
            GameListScreen(
                games = testGames,
                displayName = "Test User",
                onGameClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Welcome, Test User!")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Add game")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Profile")
            .assertIsDisplayed()
    }

    @Test
    fun showsEmptyMessageWhenNoGamesExist() {
        composeTestRule.setContent {
            GameListScreen(
                games = emptyList(),
                displayName = "Test User",
                onGameClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("You haven't added any games yet")
            .assertIsDisplayed()
    }

    @Test
    fun tappingGameSendsThatGame() {
        var clickedGame: Game? = null

        composeTestRule.setContent {
            GameListScreen(
                games = testGames,
                displayName = "Test User",
                onGameClick = {
                    clickedGame = it
                }
            )
        }

        composeTestRule
            .onNodeWithText("Elden Ring")
            .performClick()

        assertEquals(
            "Elden Ring",
            clickedGame?.title
        )

        assertEquals(
            "2",
            clickedGame?.id
        )
    }

    @Test
    fun tappingAddButtonCallsOnAddClick() {
        var addClicked = false

        composeTestRule.setContent {
            GameListScreen(
                games = testGames,
                displayName = "Test User",
                onGameClick = {},
                onAddClick = {
                    addClicked = true
                }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Add game")
            .performClick()

        assertTrue(addClicked)
    }

    @Test
    fun tappingProfileCircleCallsOnProfileClick() {
        var profileClicked = false

        composeTestRule.setContent {
            GameListScreen(
                games = testGames,
                displayName = "Test User",
                onGameClick = {},
                onProfileClick = {
                    profileClicked = true
                }
            )
        }
        composeTestRule
            .onNodeWithContentDescription("Profile")
            .performClick()
        assertTrue(profileClicked)
    }
}