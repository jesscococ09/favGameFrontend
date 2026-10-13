package com.example.cst438_project2.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cst438_project2.ui.screens.Game
import com.example.cst438_project2.ui.screens.GameListScreen
import com.example.cst438_project2.ui.screens.LoginScreen
import com.example.cst438_project2.ui.screens.sampleGames

@Composable
fun AppNavGraph(
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit
) {
    // Holds the current screen and navigation back stack.
    val navController = rememberNavController()

    // Decide which screen to show when the graph first opens.
    val startDestination = if (isLoggedIn) {
        FavGameRoutes.GAME_LIST
    } else {
        FavGameRoutes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Keep the existing login UI and OAuth button behavior.
        composable(FavGameRoutes.LOGIN) {
            LoginScreen(onLoginClick = onLoginClick)
        }

        // Main game library screen.
        composable(FavGameRoutes.GAME_LIST) {
            GameListScreen(
                // Temporary data until real backend data is connected.
                games = sampleGames,

                // GameListScreen passes the selected Game object here.
                onGameClick = { game: Game ->
                    // Convert the game's integer ID to a route argument.
                    navController.navigate(
                        "${FavGameRoutes.GAME_DETAILS}/${game.id}"
                    )
                },

                onProfileClick = {
                    navController.navigate(FavGameRoutes.PROFILE)
                },

                onAddClick = {
                    navController.navigate(FavGameRoutes.ADD_GAME)
                }
            )
        }

        // Game details receives the selected game's ID.
        composable(
            route = "${FavGameRoutes.GAME_DETAILS}/{gameId}",
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.IntType
                }
            )
        ) { entry ->
            // Read the ID passed from the game list.
            val gameId = entry.arguments?.getInt("gameId") ?: -1

            PlaceholderScreen(
                title = "Game Details — Game ID: $gameId",
                onBack = { navController.popBackStack() }
            )
        }

        // TODO: Replace with the real Profile screen composable.
        composable(FavGameRoutes.PROFILE) {
            PlaceholderScreen(
                title = "Profile",
                onBack = { navController.popBackStack() }
            )
        }

        // TODO: Replace with the real Add Game screen composable.
        composable(FavGameRoutes.ADD_GAME) {
            PlaceholderScreen(
                title = "Add Game",
                onBack = { navController.popBackStack() }
            )
        }
    }
}

// Temporary screens let us test navigation before integrating
// the actual details, profile, and add-game implementations.
@Composable
private fun PlaceholderScreen(
    title: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title)

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Back")
        }
    }
}