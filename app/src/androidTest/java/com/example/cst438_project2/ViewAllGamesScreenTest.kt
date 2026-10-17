package com.example.cst438_project2


import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.performTextInput
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
    private fun game(name: String, genre: String, platform: String = "PC") =
        NewGame(name, null, "", platform, genre, 0, "")


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
    fun showsTitleBackAndFilterButtons() {
        showScreen()

        composeTestRule.onNodeWithText("All Games").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Filter").assertIsDisplayed()
    }

    @Test
    fun filterPanelIsHiddenUntilFilterIsTapped() {
        showScreen(games = listOf(game("Halo", "Shooter")))

        // Hidden at first
        composeTestRule.onNodeWithContentDescription("Genre filter").assertDoesNotExist()

        composeTestRule.onNodeWithContentDescription("Filter").performClick()

        // All four filters show up
        composeTestRule.onNodeWithContentDescription("Genre filter").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Platform filter").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Rating filter").assertIsDisplayed()
        composeTestRule.onNodeWithText("Comment contains…").assertIsDisplayed()
    }

    @Test
    fun pickingPlatformFiltersTheList() {
        showScreen(
            games = listOf(
                game("Halo", "Shooter", platform = "PC"),
                game("Celeste", "Platformer", platform = "Nintendo Switch")
            )
        )

        composeTestRule.onNodeWithContentDescription("Filter").performClick()
        composeTestRule.onNodeWithContentDescription("Platform filter").performClick()
        composeTestRule.onNodeWithText("Nintendo Switch").performClick()

        // Only the Switch game is left, and the count shows "1 of 2"
        composeTestRule.onNodeWithText("Celeste").assertIsDisplayed()
        composeTestRule.onNodeWithText("Halo").assertDoesNotExist()
        composeTestRule.onNodeWithText("1 of 2 games").assertIsDisplayed()
    }

    @Test
    fun showsMessageWhenNothingMatches() {
        showScreen(games = listOf(game("Halo", "Shooter", platform = "PC")))

        composeTestRule.onNodeWithContentDescription("Filter").performClick()
        composeTestRule.onNodeWithContentDescription("Platform filter").performClick()
        composeTestRule.onNodeWithText("Mobile").performClick()

        composeTestRule.onNodeWithText("No games match your search or filters").assertIsDisplayed()
    }

    @Test
    fun clearFiltersShowsEverythingAgain() {
        showScreen(
            games = listOf(
                game("Halo", "Shooter", platform = "PC"),
                game("Celeste", "Platformer", platform = "Nintendo Switch")
            )
        )

        composeTestRule.onNodeWithContentDescription("Filter").performClick()
        composeTestRule.onNodeWithContentDescription("Platform filter").performClick()
        composeTestRule.onNodeWithText("PC").performClick()
        composeTestRule.onNodeWithText("Celeste").assertDoesNotExist()

        composeTestRule.onNodeWithText("Clear filters").performClick()

        composeTestRule.onNodeWithText("Celeste").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 games").assertIsDisplayed()
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

    // ---------- Search bar ----------

    // The search bar is the only text box on screen while the filter panel is closed
    private fun typeInSearch(text: String) {
        composeTestRule.onNode(hasSetTextAction()).performTextInput(text)
    }

    @Test
    fun searchBarIsShownNextToFilter() {
        showScreen(games = listOf(game("Halo", "Shooter")))

        composeTestRule.onNodeWithText("Search games…").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Filter").assertIsDisplayed()
    }

    @Test
    fun typingInSearchNarrowsTheList() {
        showScreen(games = listOf(game("Halo", "Shooter"), game("Stardew Valley", "Simulation")))

        typeInSearch("star")

        composeTestRule.onNodeWithText("Stardew Valley").assertIsDisplayed()
        composeTestRule.onNodeWithText("Halo").assertDoesNotExist()
        composeTestRule.onNodeWithText("1 of 2 games").assertIsDisplayed()
    }

    @Test
    fun clearSearchShowsEverythingAgain() {
        showScreen(games = listOf(game("Halo", "Shooter"), game("Stardew Valley", "Simulation")))

        typeInSearch("zzz")
        composeTestRule.onNodeWithText("No games match your search or filters").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Clear search").performClick()

        composeTestRule.onNodeWithText("Halo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Stardew Valley").assertIsDisplayed()
    }

    // ---------- Sort (inside the filter panel) ----------

    // How far down the screen a game's title is (smaller = higher up)
    private fun topOf(text: String) =
        composeTestRule.onNodeWithText(text).getUnclippedBoundsInRoot().top

    @Test
    fun choosingAZSortsTheList() {
        showScreen(games = listOf(game("Zelda", "Adventure"), game("Halo", "Shooter")))

        // Order added: Zelda first
        assertTrue(topOf("Zelda") < topOf("Halo"))

        composeTestRule.onNodeWithContentDescription("Filter").performClick()
        composeTestRule.onNodeWithContentDescription("Sort filter").performClick()
        composeTestRule.onNodeWithText("A–Z").performClick()

        // A–Z: Halo first
        assertTrue(topOf("Halo") < topOf("Zelda"))
    }

    @Test
    fun choosingOrderAddedUndoesTheSort() {
        showScreen(games = listOf(game("Zelda", "Adventure"), game("Halo", "Shooter")))

        composeTestRule.onNodeWithContentDescription("Filter").performClick()
        composeTestRule.onNodeWithContentDescription("Sort filter").performClick()
        composeTestRule.onNodeWithText("A–Z").performClick()

        composeTestRule.onNodeWithContentDescription("Sort filter").performClick()
        composeTestRule.onNodeWithText("Order added").performClick()

        assertTrue(topOf("Zelda") < topOf("Halo"))
    }
}
