package com.example.cst438_project2

import com.example.cst438_project2.ui.screens.GameFilters
import com.example.cst438_project2.ui.screens.NewGame
import com.example.cst438_project2.ui.screens.filterGames
import org.junit.Assert
import org.junit.Test

// Plain unit tests for the View All Games filter logic (no emulator needed)
class GameFilterTest {

    private val halo = NewGame("Halo", null, "", "PC", "Shooter", 9, "Great multiplayer")
    private val stardew = NewGame("Stardew Valley", null, "", "PC", "Simulation", 10, "So relaxing")
    private val zelda = NewGame("Zelda", null, "", "Nintendo Switch", "Adventure", 7, "")
    private val celeste =
        NewGame("Celeste", null, "", "Nintendo Switch", "Platformer", 0, "Hard but GREAT")
    private val all = listOf(halo, stardew, zelda, celeste)

    @Test
    fun noFiltersReturnsEveryGame() {
        Assert.assertEquals(all, filterGames(all, GameFilters()))
    }

    @Test
    fun filtersByGenre() {
        Assert.assertEquals(listOf(halo), filterGames(all, GameFilters(genre = "Shooter")))
    }

    @Test
    fun filtersByPlatform() {
        Assert.assertEquals(
            listOf(zelda, celeste),
            filterGames(all, GameFilters(platform = "Nintendo Switch"))
        )
    }

    @Test
    fun minRatingKeepsThatRatingAndHigher() {
        Assert.assertEquals(listOf(halo, stardew), filterGames(all, GameFilters(minRating = 9)))
    }

    @Test
    fun minRatingHidesUnratedGames() {
        // Celeste has rating 0 (not rated), so even "1+" hides it
        Assert.assertEquals(
            listOf(halo, stardew, zelda),
            filterGames(all, GameFilters(minRating = 1))
        )
    }

    @Test
    fun commentSearchIgnoresCaseAndSpaces() {
        Assert.assertEquals(
            listOf(halo, celeste),
            filterGames(all, GameFilters(commentQuery = "  great "))
        )
    }
// checks filter
    @Test
    fun blankCommentSearchDoesNotFilter() {
    Assert.assertEquals(all, filterGames(all, GameFilters(commentQuery = "   ")))
    }

    // tests if filters match
    @Test
    fun combinedFiltersMustAllMatch() {
        val filters = GameFilters(platform = "PC", minRating = 10)
        Assert.assertEquals(listOf(stardew), filterGames(all, filters))
    }

    // checks if empty
    @Test
    fun noMatchesReturnsEmptyList() {
        Assert.assertTrue(filterGames(all, GameFilters(genre = "Sports")).isEmpty())
    }

    @Test
    fun isActiveOnlyWhenSomethingIsSet() {
        Assert.assertFalse(GameFilters().isActive)
        Assert.assertFalse(GameFilters(commentQuery = "  ").isActive)
        Assert.assertTrue(GameFilters(genre = "RPG").isActive)
        Assert.assertTrue(GameFilters(minRating = 5).isActive)
    }
}