package com.example.cst438_project2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.example.cst438_project2.ui.screens.ChangeAllAttributesScreen
import com.example.cst438_project2.ui.screens.Game
import com.example.cst438_project2.ui.screens.NewGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ChangeAllAttributesScreenTest {

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
        onBackClick: () -> Unit = {},
        onSaveClick: (NewGame) -> Unit = {}
    ) {
        composeTestRule.setContent {
            ChangeAllAttributesScreen(
                game = testGame,
                onBackClick = onBackClick,
                onSaveClick = onSaveClick
            )
        }
    }

    @Test
    fun showsTitleAndBackButton() {
        showScreen()

        composeTestRule
            .onNodeWithText("Change All Attributes")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertIsDisplayed()
    }

    @Test
    fun showsCurrentGameName() {
        showScreen()

        composeTestRule
            .onNodeWithText("Halo")
            .assertIsDisplayed()
    }

    @Test
    fun showsCurrentDescription() {
        showScreen()

        composeTestRule
            .onNodeWithText("Space shooter")
            .assertIsDisplayed()
    }

    @Test
    fun showsCurrentPlatform() {
        showScreen()

        composeTestRule
            .onNodeWithText("PC")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun showsCurrentGenre() {
        showScreen()

        composeTestRule
            .onNodeWithText("Shooter")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun showsCurrentRating() {
        showScreen()

        composeTestRule
            .onNodeWithText("8")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun showsCurrentComment() {
        showScreen()

        composeTestRule
            .onNodeWithText("Great game")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun showsAllFieldLabels() {
        showScreen()

        listOf(
            "Picture",
            "Game Name",
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
    fun showsSaveButton() {
        showScreen()

        composeTestRule
            .onNodeWithText("Save All Changes")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun tappingBackCallsOnBackClick() {
        var backClicked = false

        showScreen(
            onBackClick = {
                backClicked = true
            }
        )

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assertTrue(backClicked)
    }

    @Test
    fun changingGameNameUpdatesField() {
        showScreen()

        composeTestRule
            .onNodeWithText("Halo")
            .performTextClearance()

        composeTestRule
            .onNodeWithText("Enter game name")
            .performTextInput("Halo 2")

        composeTestRule
            .onNodeWithText("Halo 2")
            .assertIsDisplayed()
    }

    @Test
    fun saveSendsAllCurrentGameValues() {
        var savedGame: NewGame? = null

        showScreen(
            onSaveClick = {
                savedGame = it
            }
        )

        composeTestRule
            .onNodeWithText("Save All Changes")
            .performScrollTo()
            .performClick()

        assertNotNull(savedGame)

        assertEquals(
            "Halo",
            savedGame?.gameName
        )

        assertEquals(
            "Space shooter",
            savedGame?.gameDescription
        )

        assertEquals(
            "PC",
            savedGame?.platform
        )

        assertEquals(
            "Shooter",
            savedGame?.genre
        )

        assertEquals(
            8,
            savedGame?.rating
        )

        assertEquals(
            "Great game",
            savedGame?.comment
        )
    }
}