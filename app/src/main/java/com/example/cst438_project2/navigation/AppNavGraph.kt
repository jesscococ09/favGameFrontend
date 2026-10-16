package com.example.cst438_project2.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cst438_project2.ui.screens.AddGameScreen
import com.example.cst438_project2.ui.screens.Game
import com.example.cst438_project2.ui.screens.GameDetailsPopup
import com.example.cst438_project2.ui.screens.GameListScreen
import com.example.cst438_project2.ui.screens.LoginScreen
import com.example.cst438_project2.ui.screens.NewGame
import com.example.cst438_project2.ui.screens.SettingsScreen
import com.example.cst438_project2.ui.screens.ViewAllGamesScreen
import com.example.cst438_project2.ui.viewmodel.AdminViewModel
import com.example.cst438_project2.ui.viewmodel.ApiViewModel
import com.example.cst438_project2.ui.viewmodel.RecommendedGamesViewModel
import com.example.cst438_project2.ui.viewmodel.UserViewModel

@Composable
fun AppNavGraph(
    isLoggedIn: Boolean,
    token: String?,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit = {},
    userViewModel: UserViewModel? = null,
    apiViewModel: ApiViewModel? = null,
    recommendedGamesViewModel: RecommendedGamesViewModel? = null,
    adminViewModel: AdminViewModel? = null,

    // Tests can provide their own controller.
    // Normal app usage creates one automatically.
    navController: NavHostController = rememberNavController()
) {
    val user = userViewModel
        ?.user
        ?.collectAsState()
        ?.value

    val apiGames = apiViewModel
        ?.games
        ?.collectAsState()
        ?.value
        .orEmpty()

    // Convert Api data into the model used by the UI.
    val displayedGames = apiGames.map { apiGame ->
        Game(
            id = apiGame.id,
            title = apiGame.gameName,
            imageUrl = apiGame.thumbnail,
            updates = 0,
            description = apiGame.gameDescription,
            platform = apiGame.platform,
            genre = apiGame.genre,
            rating = apiGame.rating,
            comment = apiGame.comment
        )
    }

    // Temporary until ViewAllGamesScreen keeps the backend UUID.
    val viewAllGames = apiGames.map { apiGame ->
        NewGame(
            gameName = apiGame.gameName,
            thumbnail = apiGame.thumbnail,
            gameDescription = apiGame.gameDescription,
            platform = apiGame.platform,
            genre = apiGame.genre,
            rating = apiGame.rating,
            comment = apiGame.comment
        )
    }
    val isAdmin = user?.isAdmin == true
    val profilePhotoUrl = user?.iconUrl
    val displayName = user?.displayName.orEmpty()

    @Suppress("UNUSED_VARIABLE")
    val currentAdminViewModel = adminViewModel

    // Decide which screen to show when the graph first opens.
    val startDestination = if (isLoggedIn) {
        FavGameRoutes.GAME_LIST
    } else {
        FavGameRoutes.LOGIN
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // Login screen.
        composable(FavGameRoutes.LOGIN) {
            LoginScreen(onLoginClick = onLoginClick)
        }

        // Home screen.
        composable(FavGameRoutes.GAME_LIST) {
            GameListScreen(
                games = displayedGames,
                displayName = displayName,
                profilePhotoUrl = profilePhotoUrl,

                onGameClick = { game ->
                    navController.navigate("${FavGameRoutes.GAME_DETAILS}/${game.id}") },
                onProfileClick = {
                    navController.navigate(
                        FavGameRoutes.SETTINGS
                    ) {
                        launchSingleTop = true
                    }
                },

                onAddClick = {
                    navController.navigate("${FavGameRoutes.ADD_GAME}/api") },
                onViewAllClick = {
                    navController.navigate(FavGameRoutes.VIEW_ALL_GAMES) {
                        launchSingleTop = true
                    }
                },

                onRecommendedClick = {
                    navController.navigate(
                        FavGameRoutes.RECOMMENDED_GAMES
                    ) {
                        launchSingleTop = true
                    }
                },

                // Delete selected personal games by UUID.
                onDeleteGames = { selectedIds ->
                    val accessToken = token
                    if (accessToken != null) {
                        apiViewModel?.deleteGames(accessToken, selectedIds)
                    }
                }
            )
        }

        // All personal games.
        composable(FavGameRoutes.VIEW_ALL_GAMES) {
            ViewAllGamesScreen(
                games = viewAllGames,
                // View All always goes back to Home.
                onBackClick = {
                    navController.navigate(
                        FavGameRoutes.GAME_LIST
                    ) {
                        launchSingleTop = true
                    }
                },

                onGameClick = { selectedGame ->
                    // Temporary lookup because NewGame has no UUID.
                    val apiGame = apiGames.find {
                        it.gameName == selectedGame.gameName &&
                                it.thumbnail == (selectedGame.thumbnail ?: "") &&
                                it.gameDescription == selectedGame.gameDescription &&
                                it.platform == selectedGame.platform &&
                                it.genre == selectedGame.genre &&
                                it.rating == selectedGame.rating &&
                                it.comment == selectedGame.comment
                    }
                    if (apiGame != null) {
                        navController.navigate("${FavGameRoutes.GAME_DETAILS}/${apiGame.id}")
                    }
                }
            )
        }
        // Game attributes can be opened from multiple screens, so using popBackStack instead of loc
        dialog(
            route = "${FavGameRoutes.GAME_DETAILS}/{gameId}",
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val gameId = entry.arguments?.getString("gameId").orEmpty()
            val game = displayedGames.find { it.id == gameId }
            if (game != null) {
                GameDetailsPopup(
                    game = game,
                    // Return to the screen that opened the popup.
                    onClose = { navController.popBackStack() }
                )
            } else {
                PlaceholderScreen(
                    title = "Game not found",
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        // Settings / Profile.
        composable(FavGameRoutes.SETTINGS) {
            SettingsScreen(
                // Settings always goes back to Home.
                onBackClick = {
                    navController.navigate(
                        FavGameRoutes.GAME_LIST
                    ) {
                        launchSingleTop = true
                    }
                },
                onLogoutClick = {
                    onLogoutClick()
                    // Prevent logged-out users from returning to Home.
                    navController.navigate(
                        FavGameRoutes.LOGIN
                    ) {
                        popUpTo(FavGameRoutes.GAME_LIST) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                isAdmin = isAdmin
            )
        }

        // AddGame can be used for personal or recommended games.
        composable(
            route = "${FavGameRoutes.ADD_GAME}/{target}",
            arguments = listOf(
                navArgument("target") {
                    type = NavType.StringType
                    defaultValue = "api"
                }
            )
        ) { entry ->
            val target = entry.arguments?.getString("target") ?: "api"
            AddGameScreen(

                // AddGame can be opened from multiple locations, so using popBAck stack instead of loc
                onBackClick = { navController.popBackStack() },
                onSaveClick = { newGame ->
                    val accessToken = token
                    if (accessToken != null) {
                        val body: Map<String, Any> = mapOf(
                            "gameName" to newGame.gameName,
                            "thumbnail" to (newGame.thumbnail ?: ""),
                            "gameDescription" to newGame.gameDescription,
                            "platform" to newGame.platform,
                            "genre" to newGame.genre,
                            "rating" to newGame.rating,
                            "comment" to newGame.comment
                        )
                        if (target == "recommended") {
                            // Only admins can add recommended games.
                            if (isAdmin) {
                                recommendedGamesViewModel
                                    ?.createRecommendation(
                                        accessToken,
                                        body
                                    )
                                navController.popBackStack()
                            }
                        } else {
                            // Personal games are stored in Api.
                            apiViewModel?.addGame(accessToken, body)
                            navController.popBackStack()
                        }
                    }
                }
            )
        }

        // TODO:ADD RecommendedGamesScreen
        composable(FavGameRoutes.RECOMMENDED_GAMES) {
            PlaceholderScreen(
                title = "Recommended Games",
                onBack = {
                    navController.navigate(
                        FavGameRoutes.GAME_LIST
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

// Temporary screen until the real destination is connected.
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
        Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
            Text("Back")
        }
    }
}