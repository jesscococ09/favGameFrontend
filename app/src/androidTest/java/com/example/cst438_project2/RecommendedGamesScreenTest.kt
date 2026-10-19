package com.example.cst438_project2.ui.screens

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RecommendedGamesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun game(
        id: String = "1",
        title: String = "Elden Ring",
        genre: String = "Action RPG",
        platform: String = "PC"
    ) = Game(
        id = id,
        title = title,
        imageUrl = "",
        updates = 0,
        description = "Test description",
        platform = platform,
        genre = genre,
        rating = 8,
        comment = ""
    )

    private fun showScreen(
        games: List<Game> = emptyList(),
        isAdmin: Boolean = false,
        loading: Boolean = false,
        error: String? = null,
        onBackClick: () -> Unit = {},
        onAddClick: () -> Unit = {},
        onGameClick: (Game) -> Unit = {},
        onDeleteGame: (String) -> Unit = {},
        onRetry: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            RecommendedGamesScreen(
                games = games,
                isAdmin = isAdmin,
                loading = loading,
                error = error,
                onBackClick = onBackClick,
                onAddClick = onAddClick,
                onGameClick = onGameClick,
                onDeleteGame = onDeleteGame,
                onRetry = onRetry
            )
        }
    }

    @Test
    fun screen_displaysTitle() {
        showScreen()

        composeTestRule
            .onNodeWithText("Recommended Games")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Discover your next favorite")
            .assertIsDisplayed()
    }

    @Test
    fun backButton_invokesCallback() {
        var backCalled = false

        showScreen(
            onBackClick = {
                backCalled = true
            }
        )

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assertTrue(backCalled)
    }

    @Test
    fun regularUser_doesNotSeeAddButton() {
        showScreen(
            isAdmin = false
        )

        composeTestRule
            .onNodeWithContentDescription(
                "Add recommended game"
            )
            .assertDoesNotExist()
    }

    @Test
    fun admin_seesAddButton() {
        showScreen(
            isAdmin = true
        )

        composeTestRule
            .onNodeWithContentDescription(
                "Add recommended game"
            )
            .assertIsDisplayed()
    }

    @Test
    fun adminAddButton_invokesCallback() {
        var addCalled = false

        showScreen(
            isAdmin = true,
            onAddClick = {
                addCalled = true
            }
        )

        composeTestRule
            .onNodeWithContentDescription(
                "Add recommended game"
            )
            .performClick()

        assertTrue(addCalled)
    }

    @Test
    fun regularUser_doesNotSeeRemoveButton() {
        showScreen(
            games = listOf(
                game()
            ),
            isAdmin = false
        )

        composeTestRule
            .onAllNodesWithText(
                text = "Remove",
                substring = false
            )
            .assertCountEquals(0)
    }

    @Test
    fun admin_seesRemoveButton() {
        showScreen(
            games = listOf(
                game()
            ),
            isAdmin = true
        )

        composeTestRule
            .onNodeWithText(
                text = "Remove",
                substring = false
            )
            .assertIsDisplayed()
    }

    @Test
    fun removeButton_opensConfirmationDialog() {
        var deleteCalled = false

        showScreen(
            games = listOf(
                game(
                    id = "game-1",
                    title = "Elden Ring"
                )
            ),
            isAdmin = true,
            onDeleteGame = {
                deleteCalled = true
            }
        )

        composeTestRule
            .onNodeWithText(
                text = "Remove",
                substring = false
            )
            .performClick()

        composeTestRule
            .onNodeWithText(
                "Remove recommendation?"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "Remove “Elden Ring” from the shared " +
                        "recommended games list? This will not delete " +
                        "it from users’ personal libraries."
            )
            .assertIsDisplayed()

        assertFalse(deleteCalled)
    }

    @Test
    fun cancelRemove_doesNotInvokeCallback() {
        var deleteCalled = false

        showScreen(
            games = listOf(
                game(
                    id = "game-1"
                )
            ),
            isAdmin = true,
            onDeleteGame = {
                deleteCalled = true
            }
        )

        composeTestRule
            .onNodeWithText(
                text = "Remove",
                substring = false
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Cancel")
            .performClick()

        composeTestRule
            .onNodeWithText(
                "Remove recommendation?"
            )
            .assertDoesNotExist()

        assertFalse(deleteCalled)
    }

    @Test
    fun confirmRemove_invokesCallbackWithGameId() {
        var deletedGameId = ""

        showScreen(
            games = listOf(
                game(
                    id = "game-123",
                    title = "Elden Ring"
                )
            ),
            isAdmin = true,
            onDeleteGame = { gameId ->
                deletedGameId = gameId
            }
        )

        composeTestRule
            .onNodeWithText(
                text = "Remove",
                substring = false
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Confirm removal")
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                "game-123",
                deletedGameId
            )
        }

        composeTestRule
            .onNodeWithText(
                "Remove recommendation?"
            )
            .assertDoesNotExist()
    }

    @Test
    fun viewDetails_invokesGameClickCallback() {
        var selectedGame: Game? = null

        val testGame = game(
            id = "game-1",
            title = "Elden Ring"
        )

        showScreen(
            games = listOf(testGame),
            onGameClick = { game ->
                selectedGame = game
            }
        )

        composeTestRule
            .onNodeWithText("View details")
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                testGame,
                selectedGame
            )
        }
    }

    @Test
    fun gameInformation_isDisplayed() {
        showScreen(
            games = listOf(
                game(
                    title = "Elden Ring",
                    genre = "Action RPG",
                    platform = "PC"
                )
            )
        )

        composeTestRule
            .onNodeWithText("Elden Ring")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Action RPG • PC")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Test description")
            .assertIsDisplayed()
    }

    @Test
    fun loadingState_isDisplayed() {
        showScreen(
            games = emptyList(),
            loading = true
        )

        composeTestRule
            .onNodeWithText(
                "Loading recommendations…"
            )
            .assertIsDisplayed()
    }

    @Test
    fun errorState_isDisplayed() {
        showScreen(
            games = emptyList(),
            loading = false,
            error = "Network error"
        )

        composeTestRule
            .onNodeWithText(
                "Couldn't load recommendations."
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Network error")
            .assertIsDisplayed()
    }

    @Test
    fun retryButton_invokesCallback() {
        var retryCalled = false

        showScreen(
            games = emptyList(),
            error = "Network error",
            onRetry = {
                retryCalled = true
            }
        )

        composeTestRule
            .onNodeWithText("Try again")
            .performClick()

        assertTrue(retryCalled)
    }

    @Test
    fun emptyRegularUser_seesEmptyMessage() {
        showScreen(
            games = emptyList(),
            isAdmin = false
        )

        composeTestRule
            .onNodeWithText(
                "No recommendations yet."
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "Check back later for new games."
            )
            .assertIsDisplayed()
    }

    @Test
    fun emptyAdmin_seesAddMessage() {
        showScreen(
            games = emptyList(),
            isAdmin = true
        )

        composeTestRule
            .onNodeWithText(
                "No recommendations yet."
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "Add a game to start the list."
            )
            .assertIsDisplayed()
    }

    @Test
    fun errorWithExistingGames_showsRefreshError() {
        showScreen(
            games = listOf(
                game()
            ),
            error = "Refresh failed"
        )

        composeTestRule
            .onNodeWithText(
                "Could not refresh recommendations: Refresh failed"
            )
            .assertIsDisplayed()
    }

    @Test
    fun fourOrFewerGames_areAllSelected() {
        val games = listOf(
            game(
                id = "1",
                title = "Game 1"
            ),
            game(
                id = "2",
                title = "Game 2"
            ),
            game(
                id = "3",
                title = "Game 3"
            )
        )

        val result = selectRecommendedGames(games)

        assertEquals(
            3,
            result.size
        )

        assertTrue(
            result.containsAll(games)
        )
    }

    @Test
    fun moreThanFourGames_returnsFour() {
        val games = listOf(
            game(
                id = "1",
                title = "Game 1"
            ),
            game(
                id = "2",
                title = "Game 2"
            ),
            game(
                id = "3",
                title = "Game 3"
            ),
            game(
                id = "4",
                title = "Game 4"
            ),
            game(
                id = "5",
                title = "Game 5"
            )
        )

        val result = selectRecommendedGames(games)

        assertEquals(
            4,
            result.size
        )

        assertTrue(
            games.containsAll(result)
        )
    }

    @Test
    fun zeroLimit_returnsEmptyList() {
        val games = listOf(
            game(
                id = "1"
            )
        )

        val result = selectRecommendedGames(
            games,
            limit = 0
        )

        assertTrue(
            result.isEmpty()
        )
    }

    @Test
    fun negativeLimit_returnsEmptyList() {
        val games = listOf(
            game(
                id = "1"
            )
        )

        val result = selectRecommendedGames(
            games,
            limit = -1
        )

        assertTrue(
            result.isEmpty()
        )
    }
}