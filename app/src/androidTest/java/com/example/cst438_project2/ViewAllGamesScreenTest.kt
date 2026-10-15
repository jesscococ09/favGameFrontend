package com.example.cst438_project2

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.cst438_project2.ui.screens.NewGame
import com.example.cst438_project2.ui.screens.ViewAllGamesScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ViewAllGamesScreenTest {

    // Opens a composable on the emulator so the test can interact with it
    @get:Rule
    val composeTestRule = createComposeRule()

    // Small helper so each test only fills in the fields it cares about
    private fun game(name: String, genre: String) =
        NewGame(name, null, "", "PC", genre, 0, "")

    // Shows the View All Games screen; the list and click handlers can be swapped per test
    private fun showScreen(
        games: List<NewGame> = emptyList(),
        onBackClick: () -> Unit = { },
        onGameClick: (NewGame) -> Unit = { }
    ) {
        composeTestRule.setContent {
            ViewAllGamesScreen(
                games = games,
                onBackClick = onBackClick,
                onGameClick = onGameClick
            )
        }
    }

    @Test
    fun showsTitleBackAndSortButtons() {
        showScreen()

        composeTestRule.onNodeWithText("All Games").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Sort").assertIsDisplayed()
    }

    @Test
    fun sortButtonIsTappable() {
        showScreen(games = listOf(game("Halo", "Shooter")))

        // No sorting yet, but the button should be clickable and not crash when tapped
        composeTestRule.onNodeWithContentDescription("Sort")
            .assertHasClickAction()
            .performClick()

        composeTestRule.onNodeWithText("Halo").assertIsDisplayed()
    }

    @Test
    fun showsEmptyMessageWhenNoGamesAdded() {
        showScreen()

        composeTestRule.onNodeWithText("You haven't added any games yet").assertIsDisplayed()
    }

    @Test
    fun showsTitleAndGenreForEachGame() {
        showScreen(
            games = listOf(
                game("Halo", "Shooter"),
                game("Stardew Valley", "Simulation")
            )
        )

        composeTestRule.onNodeWithText("Halo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Shooter").assertIsDisplayed()
        composeTestRule.onNodeWithText("Stardew Valley").assertIsDisplayed()
        composeTestRule.onNodeWithText("Simulation").assertIsDisplayed()

        // The empty message should be gone once there are games
        composeTestRule.onNodeWithText("You haven't added any games yet").assertDoesNotExist()
    }

    @Test
    fun showsDashWhenGenreIsBlank() {
        showScreen(games = listOf(game("Untitled Game", "")))

        composeTestRule.onNodeWithText("Untitled Game").assertIsDisplayed()
        composeTestRule.onNodeWithText("—").assertIsDisplayed()
    }

    @Test
    fun countSaysOneGameForASingleGame() {
        showScreen(games = listOf(game("Halo", "Shooter")))

        composeTestRule.onNodeWithText("1 game").assertIsDisplayed()
    }

    @Test
    fun countSaysGamesForMoreThanOne() {
        showScreen(
            games = listOf(
                game("Halo", "Shooter"),
                game("Celeste", "Platformer"),
                game("Hades", "Action")
            )
        )

        composeTestRule.onNodeWithText("3 games").assertIsDisplayed()
    }

    @Test
    fun tappingBackCallsOnBackClick() {
        var backClicked = false
        showScreen(onBackClick = { backClicked = true })

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        assertTrue(backClicked)
    }

    @Test
    fun tappingGameSendsThatGame() {
        var clickedGame: NewGame? = null
        val halo = game("Halo", "Shooter")
        val celeste = game("Celeste", "Platformer")
        showScreen(games = listOf(halo, celeste), onGameClick = { clickedGame = it })

        composeTestRule.onNodeWithText("Celeste").performClick()

        assertEquals(celeste, clickedGame)
    }
}
