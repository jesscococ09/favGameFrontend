package com.example.cst438_project2

import com.example.cst438_project2.ui.screens.GameFilters
import com.example.cst438_project2.ui.screens.NewGame
import com.example.cst438_project2.ui.screens.filterGames
import com.example.cst438_project2.ui.screens.sortGamesAZ
import org.junit.Assert
import org.junit.Test

// Plain unit tests for the View All Games A–Z sort (no emulator needed)
class GameOrderTest {

    private val halo = NewGame("Halo", null, "", "PC", "Shooter", 9, "")
    private val stardew = NewGame("Stardew Valley", null, "", "PC", "Simulation", 10, "")
    private val zelda = NewGame("Zelda", null, "", "Nintendo Switch", "Adventure", 7, "")
    private val celeste = NewGame("Celeste", null, "", "Nintendo Switch", "Platformer", 0, "")

    // Games in the order they were "added" (not alphabetical on purpose)
    private val all = listOf(zelda, halo, stardew, celeste)

    // Titles come out in alphabetical order
    @Test
    fun sortAZPutsTitlesInOrder() {
        Assert.assertEquals(listOf(celeste, halo, stardew, zelda), sortGamesAZ(all))
    }

    // Capital letters don't matter, so "apex" doesn't end up after "Zelda"
    @Test
    fun sortAZIgnoresCapitalLetters() {
        val apex = NewGame("apex legends", null, "", "PC", "Shooter", 8, "")
        Assert.assertEquals(listOf(apex, halo, zelda), sortGamesAZ(listOf(zelda, apex, halo)))
    }

    // Sorting an empty list doesn't crash
    @Test
    fun sortAZEmptyListStaysEmpty() {
        Assert.assertTrue(sortGamesAZ(emptyList()).isEmpty())
    }

    // Sorting doesn't change the original list, so "Order added" still works
    @Test
    fun sortAZLeavesOriginalOrderAlone() {
        sortGamesAZ(all)
        Assert.assertEquals(listOf(zelda, halo, stardew, celeste), all)
    }

    // Sort still works on a filtered list (how the screen uses it)
    @Test
    fun sortAZWorksAfterFiltering() {
        val switchGames = filterGames(all, GameFilters(platform = "Nintendo Switch"))
        Assert.assertEquals(listOf(celeste, zelda), sortGamesAZ(switchGames))
    }
}