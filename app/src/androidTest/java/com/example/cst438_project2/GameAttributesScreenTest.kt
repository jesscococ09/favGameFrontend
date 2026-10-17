package com.example.cst438_project2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.cst438_project2.ui.screens.Game
import com.example.cst438_project2.ui.screens.GameDetailsPopup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GameAttributesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testGame = Game(
        id = "test-game-id",
        title = "Halo",
        imageUrl = "",
        updates = 0,
        description = "Space shooter",
        platform = "PC",
        genre = "Shooter",
        rating = 8,
        comment = "Great game"
    )

    private fun showScreen(
        canEdit: Boolean = true,
        onChangeRating: (Int) -> Unit = {},
        onChangeComment: (String) -> Unit = {},
        onChangeAll: () -> Unit = {},
        onClose: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            GameDetailsPopup(
                game = testGame,
                canEdit = canEdit,
                onChangeRating = onChangeRating,
                onChangeComment = onChangeComment,
                onChangeAll = onChangeAll,
                onClose = onClose
            )
        }
    }

    @Test
    fun showsGameTitle() {
        showScreen()

        composeTestRule
            .onNodeWithText("Halo")
            .assertIsDisplayed()
    }

    @Test
    fun showsGameDescription() {
        showScreen()

        composeTestRule
            .onNodeWithText("Space shooter")
            .assertIsDisplayed()
    }

    @Test
    fun showsGamePlatform() {
        showScreen()

        composeTestRule
            .onNodeWithText("PC")
            .assertIsDisplayed()
    }

    @Test
    fun showsGameGenre() {
        showScreen()

        composeTestRule
            .onNodeWithText("Shooter")
            .assertIsDisplayed()
    }

    @Test
    fun showsGameRating() {
        showScreen()

        composeTestRule
            .onNodeWithText("8/10")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun showsGameComment() {
        showScreen()

        composeTestRule
            .onNodeWithText("Great game")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun showsAllAttributeLabels() {
        showScreen()

        listOf(
            "Description",
            "Platform",
            "Genre",
            "Rating",
            "Comment"
        ).forEach { label ->
            composeTestRule
                .onNodeWithText(label)
                .performScrollTo()
                .assertIsDisplayed()
        }
    }

    @Test
    fun showsNoCoverImageWhenImageIsEmpty() {
        showScreen()

        composeTestRule
            .onNodeWithText("No cover image")
            .assertIsDisplayed()
    }

    @Test
    fun editableGameShowsChangeButtons() {
        showScreen(canEdit = true)

        assertEquals(
            2,
            composeTestRule
                .onAllNodesWithText("Change")
                .fetchSemanticsNodes()
                .size
        )
    }

    @Test
    fun editableGameShowsChangeAllAttributesButton() {
        showScreen(canEdit = true)

        composeTestRule
            .onNodeWithText("Change All Attributes")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun readOnlyGameDoesNotShowChangeButtons() {
        showScreen(canEdit = false)

        assertEquals(
            0,
            composeTestRule
                .onAllNodesWithText("Change")
                .fetchSemanticsNodes()
                .size
        )
    }

    @Test
    fun readOnlyGameDoesNotShowChangeAllAttributesButton() {
        showScreen(canEdit = false)

        composeTestRule
            .onNodeWithText("Change All Attributes")
            .assertDoesNotExist()
    }

    @Test
    fun tappingCloseCallsOnClose() {
        var closeClicked = false

        showScreen(
            onClose = {
                closeClicked = true
            }
        )

        composeTestRule
            .onNodeWithText("Close")
            .performClick()

        assertTrue(closeClicked)
    }

    @Test
    fun tappingBackToMyGamesCallsOnClose() {
        var closeClicked = false

        showScreen(
            onClose = {
                closeClicked = true
            }
        )

        composeTestRule
            .onNodeWithText("Back to My Games")
            .performScrollTo()
            .performClick()

        assertTrue(closeClicked)
    }

    @Test
    fun tappingChangeAllCallsOnChangeAll() {
        var changeAllClicked = false

        showScreen(
            onChangeAll = {
                changeAllClicked = true
            }
        )

        composeTestRule
            .onNodeWithText("Change All Attributes")
            .performScrollTo()
            .performClick()

        assertTrue(changeAllClicked)
    }

    @Test
    fun ratingChangeOpensRatingPopup() {
        showScreen()

        composeTestRule
            .onAllNodesWithText("Change")[0]
            .performClick()

        composeTestRule
            .onNodeWithText("Change Rating")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Select a rating from 1 to 10.")
            .assertIsDisplayed()
    }

    @Test
    fun ratingPopupShowsCurrentRating() {
        showScreen()

        composeTestRule
            .onAllNodesWithText("Change")[0]
            .performClick()

        assertEquals(
            2,
            composeTestRule
                .onAllNodesWithText("8/10")
                .fetchSemanticsNodes()
                .size
        )
    }

    @Test
    fun commentChangeOpensCommentPopup() {
        showScreen()

        composeTestRule
            .onAllNodesWithText("Change")[1]
            .performClick()

        composeTestRule
            .onNodeWithText("Change Comment")
            .assertIsDisplayed()
    }

    @Test
    fun changeAllIsHiddenWhenGameCannotBeEdited() {
        showScreen(canEdit = false)

        composeTestRule
            .onNodeWithText("Change All Attributes")
            .assertDoesNotExist()
    }
}