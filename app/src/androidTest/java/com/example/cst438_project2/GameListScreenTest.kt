package com.example.cst438_project2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GameListScreenTest {

    // Opens a composable on the emulator so the test can interact with it
    @get:Rule
    val composeTestRule = createComposeRule()

    // Small test list so every card fits on screen
    private val testGames = listOf(
        Game(1, "Minecraft", "https://example.com/minecraft.jpg"),
        Game(2, "Elden Ring", "https://example.com/elden-ring.jpg"),
        Game(3, "Celeste", "https://example.com/celeste.jpg")
    )

    @Test
    fun showsGameCountAndTitles() {
        composeTestRule.setContent {
            GameListScreen(games = testGames, onGameClick = { })
        }

        composeTestRule.onNodeWithText("Games (3)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Minecraft").assertIsDisplayed()
        composeTestRule.onNodeWithText("Elden Ring").assertIsDisplayed()
    }

    @Test
    fun showsSearchBarAddButtonAndProfileCircle() {
        composeTestRule.setContent {
            GameListScreen(games = testGames, onGameClick = { })
        }

        composeTestRule.onNodeWithText("Search games").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Profile").assertIsDisplayed()
    }

    @Test
    fun searchFiltersGames() {
        composeTestRule.setContent {
            GameListScreen(games = testGames, onGameClick = { })
        }

        // Type into the search bar
        composeTestRule.onNode(hasSetTextAction()).performTextInput("mine")

        composeTestRule.onNodeWithText("Games (1)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Minecraft").assertIsDisplayed()
        composeTestRule.onNodeWithText("Elden Ring").assertDoesNotExist()
        composeTestRule.onNodeWithText("Celeste").assertDoesNotExist()
    }

    @Test
    fun searchIgnoresUpperAndLowerCase() {
        composeTestRule.setContent {
            GameListScreen(games = testGames, onGameClick = { })
        }

        composeTestRule.onNode(hasSetTextAction()).performTextInput("ELDEN")

        composeTestRule.onNodeWithText("Elden Ring").assertIsDisplayed()
        composeTestRule.onNodeWithText("Games (1)").assertIsDisplayed()
    }

    @Test
    fun searchWithNoMatchesShowsZero() {
        composeTestRule.setContent {
            GameListScreen(games = testGames, onGameClick = { })
        }

        composeTestRule.onNode(hasSetTextAction()).performTextInput("zzzz")

        composeTestRule.onNodeWithText("Games (0)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Minecraft").assertDoesNotExist()
    }

    @Test
    fun tappingGameSendsThatGame() {
        var clickedGame: Game? = null
        composeTestRule.setContent {
            GameListScreen(games = testGames, onGameClick = { clickedGame = it })
        }

        composeTestRule.onNodeWithText("Elden Ring").performClick()

        assertEquals("Elden Ring", clickedGame?.title)
    }

    @Test
    fun tappingAddButtonCallsOnAddClick() {
        var addClicked = false
        composeTestRule.setContent {
            GameListScreen(
                games = testGames,
                onGameClick = { },
                onAddClick = { addClicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Add").performClick()

        assertTrue(addClicked)
    }

    @Test
    fun tappingProfileCircleCallsOnProfileClick() {
        var profileClicked = false
        composeTestRule.setContent {
            GameListScreen(
                games = testGames,
                onGameClick = { },
                onProfileClick = { profileClicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Profile").performClick()

        assertTrue(profileClicked)
    }
}