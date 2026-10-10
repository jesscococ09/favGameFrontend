package com.example.cst438_project2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.example.cst438_project2.ui.screens.AddGameScreen
import com.example.cst438_project2.ui.screens.NewGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AddGameScreenTest {

    // Opens a composable on the emulator so the test can interact with it
    @get:Rule
    val composeTestRule = createComposeRule()

    // Shows the Add Game screen; the click handlers can be swapped out per test
    private fun showScreen(
        onBackClick: () -> Unit = { },
        onSaveClick: (NewGame) -> Unit = { }
    ) {
        composeTestRule.setContent {
            AddGameScreen(onBackClick = onBackClick, onSaveClick = onSaveClick)
        }
    }

    // The form is taller than the screen, so most checks scroll to the item first

    @Test
    fun showsTitleAndBackButton() {
        showScreen()

        composeTestRule.onNodeWithText("Add Game").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun showsPictureBox() {
        showScreen()

        composeTestRule.onNodeWithText("Tap to add a picture").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add picture").assertIsDisplayed()
    }

    @Test
    fun showsAllFieldLabels() {
        showScreen()

        listOf("Picture", "Game Name", "Description", "Platform", "Genre", "Rating", "Comment")
            .forEach { label ->
                composeTestRule.onNodeWithText(label).performScrollTo().assertIsDisplayed()
            }
    }

    @Test
    fun showsPlaceholders() {
        showScreen()

        listOf(
            "Enter game name",
            "What's the game about?",
            "Select a platform",
            "Select a genre",
            "Select a rating (1-10)",
            "Add a comment (optional)"
        ).forEach { placeholder ->
            composeTestRule.onNodeWithText(placeholder).performScrollTo().assertIsDisplayed()
        }
    }

    @Test
    fun addButtonIsGreyedOutWhenNameIsEmpty() {
        showScreen()

        composeTestRule.onNodeWithText("Add to My Games")
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun addButtonStaysGreyedOutWhenNameIsOnlySpaces() {
        showScreen()

        composeTestRule.onNodeWithText("Enter game name").performTextInput("   ")

        composeTestRule.onNodeWithText("Add to My Games")
            .performScrollTo()
            .assertIsNotEnabled()
    }

    @Test
    fun addButtonTurnsOnAfterTypingName() {
        showScreen()

        composeTestRule.onNodeWithText("Enter game name").performTextInput("Halo")

        composeTestRule.onNodeWithText("Add to My Games")
            .performScrollTo()
            .assertIsEnabled()
    }

    @Test
    fun platformDropdownSelectsOption() {
        showScreen()

        composeTestRule.onNodeWithContentDescription("Platform dropdown").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Nintendo Switch").performClick()

        // Menu closes and the field now shows the choice
        composeTestRule.onNodeWithText("Nintendo Switch").assertIsDisplayed()
        composeTestRule.onNodeWithText("Select a platform").assertDoesNotExist()
    }

    @Test
    fun genreDropdownSelectsOption() {
        showScreen()

        composeTestRule.onNodeWithContentDescription("Genre dropdown").performScrollTo().performClick()
        composeTestRule.onNodeWithText("RPG").performClick()

        composeTestRule.onNodeWithText("RPG").assertIsDisplayed()
        composeTestRule.onNodeWithText("Select a genre").assertDoesNotExist()
    }

    @Test
    fun ratingDropdownHasOneToTen() {
        showScreen()

        composeTestRule.onNodeWithContentDescription("Rating dropdown").performScrollTo().performClick()

        // Lowest and highest choices are both in the menu
        composeTestRule.onNodeWithText("1").assertExists()
        composeTestRule.onNodeWithText("10").assertExists()

        composeTestRule.onNodeWithText("7").performClick()

        composeTestRule.onNodeWithText("7").assertIsDisplayed()
        composeTestRule.onNodeWithText("Select a rating (1-10)").assertDoesNotExist()
    }

    @Test
    fun tappingBackCallsOnBackClick() {
        var backClicked = false
        showScreen(onBackClick = { backClicked = true })

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        assertTrue(backClicked)
    }

    @Test
    fun tappingAddSendsEverythingEntered() {
        var savedGame: NewGame? = null
        showScreen(onSaveClick = { savedGame = it })

        composeTestRule.onNodeWithText("Enter game name").performTextInput("Halo")
        composeTestRule.onNodeWithText("What's the game about?").performTextInput("Space shooter")

        composeTestRule.onNodeWithContentDescription("Platform dropdown").performScrollTo().performClick()
        composeTestRule.onNodeWithText("PC").performClick()

        composeTestRule.onNodeWithContentDescription("Genre dropdown").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Shooter").performClick()

        composeTestRule.onNodeWithContentDescription("Rating dropdown").performScrollTo().performClick()
        composeTestRule.onNodeWithText("9").performClick()

        composeTestRule.onNodeWithText("Add a comment (optional)").performScrollTo().performTextInput("Classic")

        composeTestRule.onNodeWithText("Add to My Games").performScrollTo().performClick()

        assertNotNull(savedGame)
        assertEquals("Halo", savedGame?.gameName)
        assertEquals("Space shooter", savedGame?.gameDescription)
        assertEquals("PC", savedGame?.platform)
        assertEquals("Shooter", savedGame?.genre)
        assertEquals(9, savedGame?.rating)
        assertEquals("Classic", savedGame?.comment)
        assertNull(savedGame?.thumbnail)   // no picture was picked
    }
}
