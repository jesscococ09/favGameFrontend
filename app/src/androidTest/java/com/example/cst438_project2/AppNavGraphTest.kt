package com.example.cst438_project2.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AppNavGraphTest {

    @get:Rule
    val composeRule =
        createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: NavHostController

    private fun launchGraph(isLoggedIn: Boolean) {
        composeRule.setContent {
            val controller = rememberNavController()

            navController = controller

            AppNavGraph(
                isLoggedIn = isLoggedIn,
                token = "test-token",
                onLoginClick = {},
                onLogoutClick = {},
                navController = controller
            )
        }

        composeRule.waitForIdle()
    }

    @Test
    fun loggedInUser_startsAtGameListRoute() {
        launchGraph(isLoggedIn = true)

        composeRule.runOnIdle {
            assertEquals(
                FavGameRoutes.GAME_LIST,
                navController.currentDestination?.route
            )
        }
    }

    @Test
    fun loggedOutUser_startsAtLoginRoute() {
        launchGraph(isLoggedIn = false)

        composeRule.runOnIdle {
            assertEquals(
                FavGameRoutes.LOGIN,
                navController.currentDestination?.route
            )
        }
    }

    @Test
    fun profileRoute_displaysSettingsScreen() {
        launchGraph(isLoggedIn = true)

        composeRule
            .onNodeWithContentDescription("Profile")
            .performClick()

        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(
                FavGameRoutes.SETTINGS,
                navController.currentDestination?.route
            )
        }
    }

    @Test
    fun addGameRoute_displaysAddGameScreen() {
        launchGraph(isLoggedIn = true)

        composeRule
            .onNodeWithContentDescription("Add game")
            .performClick()

        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(
                "${FavGameRoutes.ADD_GAME}/{target}",
                navController.currentDestination?.route
            )

            assertEquals(
                "api",
                navController.currentBackStackEntry
                    ?.arguments
                    ?.getString("target")
            )
        }
    }

    @Test
    fun viewAllRoute_displaysViewAllGamesScreen() {
        launchGraph(isLoggedIn = true)

        composeRule.runOnIdle {
            navController.navigate(
                FavGameRoutes.VIEW_ALL_GAMES
            )
        }

        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(
                FavGameRoutes.VIEW_ALL_GAMES,
                navController.currentDestination?.route
            )
        }
    }

    @Test
    fun recommendedRoute_displaysRecommendedGamesScreen() {
        launchGraph(isLoggedIn = true)
        composeRule.runOnIdle {
            navController.navigate(
                FavGameRoutes.RECOMMENDED_GAMES
            )
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertEquals(
                FavGameRoutes.RECOMMENDED_GAMES,
                navController.currentDestination?.route
            )
        }
    }
}