package com.example.cst438_project2

import com.example.cst438_project2.ui.screens.GameFilters
import com.example.cst438_project2.ui.screens.NewGame
import com.example.cst438_project2.ui.screens.filterGames
import com.example.cst438_project2.ui.screens.searchGames
import org.junit.Assert
import org.junit.Test

// Plain unit tests for the View All Games search bar (no emulator needed)
class GameSearchTest {

    private val halo = NewGame("Halo", null, "", "PC", "Shooter", 9, "")
    private val stardew = NewGame("Stardew Valley", null, "", "PC", "Simulation", 10, "")
    private val zelda = NewGame("Zelda", null, "", "Nintendo Switch", "Adventure", 7, "")
    private val celeste = NewGame("Celeste", null, "", "Nintendo Switch", "Platformer", 0, "")
    private val all = listOf(halo, stardew, zelda, celeste)

    // Blank search shows everything
    @Test
    fun blankSearchReturnsEveryGame() {
        Assert.assertEquals(all, searchGames(all, ""))
        Assert.assertEquals(all, searchGames(all, "   "))
    }

    // Matches part of a title, not just the start
    @Test
    fun searchMatchesPartOfTitle() {
        Assert.assertEquals(listOf(stardew), searchGames(all, "valley"))
    }

    // Capital letters don't matter
    @Test
    fun searchIgnoresCase() {
        Assert.assertEquals(listOf(zelda), searchGames(all, "ZELDA"))
    }

    // Extra spaces around the search are ignored
    @Test
    fun searchIgnoresExtraSpaces() {
        Assert.assertEquals(listOf(halo), searchGames(all, "  halo  "))
    }

    // Can match more than one game ("st" is in Stardew and Celeste)
    @Test
    fun searchCanMatchSeveralGames() {
        Assert.assertEquals(listOf(stardew, celeste), searchGames(all, "st"))
    }

    // Nothing matches -> empty list
    @Test
    fun noMatchesReturnsEmptyList() {
        Assert.assertTrue(searchGames(all, "mario").isEmpty())
    }

    // Search only looks at titles, not genre
    @Test
    fun searchDoesNotMatchGenre() {
        Assert.assertTrue(searchGames(all, "Shooter").isEmpty())
    }

    // Search works on a filtered list (how the screen uses it)
    @Test
    fun searchWorksAfterFiltering() {
        val pcGames = filterGames(all, GameFilters(platform = "PC"))
        Assert.assertEquals(listOf(stardew), searchGames(pcGames, "st"))
    }
}
