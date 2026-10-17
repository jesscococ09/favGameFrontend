
package com.example.cst438_project2

import com.example.cst438_project2.ui.screens.Game
import com.example.cst438_project2.ui.screens.selectRecommendedGames
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendedGamesSelectionTest {

    private fun games(count: Int): List<Game> =
        (1..count).map { number ->
            Game(
                id = number.toString(),
                title = "Game $number",
                imageUrl = "",
                description = "Description $number"
            )
        }

    @Test
    fun selectionReturnsAtMostFourGames() {
        assertEquals(4, selectRecommendedGames(games(10)).size)
    }

    @Test
    fun selectionDoesNotReturnDuplicates() {
        val result = selectRecommendedGames(games(10))
        assertEquals(result.size, result.map { it.id }.toSet().size)
    }

    @Test
    fun selectionOnlyReturnsOriginalGames() {
        val allGames = games(10)
        val originalIds = allGames.map { it.id }.toSet()
        val result = selectRecommendedGames(allGames)

        assertTrue(result.all { it.id in originalIds })
    }

    @Test
    fun selectionReturnsAllGamesWhenFewerThanFourExist() {
        val allGames = games(3)
        val result = selectRecommendedGames(allGames)

        assertEquals(3, result.size)
        assertEquals(allGames.map { it.id }.toSet(), result.map { it.id }.toSet())
    }

    @Test
    fun selectionReturnsAllFourWhenExactlyFourExist() {
        assertEquals(4, selectRecommendedGames(games(4)).size)
    }

    @Test
    fun selectionHandlesEmptyList() {
        assertTrue(selectRecommendedGames(emptyList()).isEmpty())
    }

    @Test
    fun selectionHandlesZeroLimit() {
        assertTrue(selectRecommendedGames(games(5), limit = 0).isEmpty())
    }

    @Test
    fun selectionHandlesNegativeLimit() {
        assertTrue(selectRecommendedGames(games(5), limit = -2).isEmpty())
    }

    @Test
    fun selectionRespectsCustomLimit() {
        assertEquals(2, selectRecommendedGames(games(10), limit = 2).size)
    }
}
