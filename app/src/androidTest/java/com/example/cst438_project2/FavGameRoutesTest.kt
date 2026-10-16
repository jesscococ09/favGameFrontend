package com.example.cst438_project2.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class FavGameRoutesTest {

    @Test
    fun loginRoute_hasExpectedValue() {
        assertEquals("login", FavGameRoutes.LOGIN)
    }

    @Test
    fun gameListRoute_hasExpectedValue() {
        assertEquals("game_list", FavGameRoutes.GAME_LIST)
    }

    @Test
    fun viewAllGamesRoute_hasExpectedValue() {
        assertEquals("view_all_games", FavGameRoutes.VIEW_ALL_GAMES)
    }

    @Test
    fun recommendedGamesRoute_hasExpectedValue() {
        assertEquals("recommended_games", FavGameRoutes.RECOMMENDED_GAMES)
    }

    @Test
    fun gameDetailsRoute_hasExpectedValue() {
        assertEquals("game_details", FavGameRoutes.GAME_DETAILS)
    }

    @Test
    fun settingsRoute_hasExpectedValue() {
        assertEquals("settings", FavGameRoutes.SETTINGS)
    }

    @Test
    fun addGameRoute_hasExpectedValue() {
        assertEquals("add_game", FavGameRoutes.ADD_GAME)
    }
    @Test
    fun changeAllAttributesRoute_hasExpectedValue() {
        assertEquals(
            "change_all_attributes",
            FavGameRoutes.CHANGE_ALL_ATTRIBUTES
        )
    }
}