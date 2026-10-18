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
import com.example.cst438_project2.ui.screens.ChangeAllAttributesScreen
import com.example.cst438_project2.ui.screens.Game
import com.example.cst438_project2.ui.screens.GameDetailsPopup
import com.example.cst438_project2.ui.screens.GameListScreen
import com.example.cst438_project2.ui.screens.LoginScreen
import com.example.cst438_project2.ui.screens.NewGame
import com.example.cst438_project2.ui.screens.SettingsScreen
import com.example.cst438_project2.ui.screens.ViewAllGamesScreen
import com.example.cst438_project2.ui.screens.RecommendedGamesScreen
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

    val userLoading = userViewModel
        ?.loading
        ?.collectAsState()
        ?.value ?: false

    val userError = userViewModel
        ?.error
        ?.collectAsState()
        ?.value

    val deleteSucceeded = userViewModel
        ?.deleteSucceeded
        ?.collectAsState()
        ?.value ?: false

    val adminUsers = adminViewModel
        ?.users
        ?.collectAsState()
        ?.value
        .orEmpty()

    val selectedAdminUser = adminViewModel
        ?.selectedUser
        ?.collectAsState()
        ?.value

    val adminLoading = adminViewModel
        ?.loading
        ?.collectAsState()
        ?.value ?: false

    val adminError = adminViewModel
        ?.error
        ?.collectAsState()
        ?.value

    val apiGames = apiViewModel
        ?.games
        ?.collectAsState()
        ?.value
        .orEmpty()

    val recommendedGames = recommendedGamesViewModel
        ?.recommendations
        ?.collectAsState()
        ?.value
        .orEmpty()

    val recommendedLoading = recommendedGamesViewModel
        ?.loading
        ?.collectAsState()
        ?.value ?: false

    val recommendedError = recommendedGamesViewModel
        ?.error
        ?.collectAsState()
        ?.value

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

    // Convert recommended game data into the model used by the UI.
    val displayedRecommendedGames = recommendedGames.map { recommendedGame ->
        Game(
            id = recommendedGame.id,
            title = recommendedGame.gameName,
            imageUrl = recommendedGame.thumbnail,
            updates = 0,
            description = recommendedGame.gameDescription,
            platform = recommendedGame.platform,
            genre = recommendedGame.genre,
            rating = recommendedGame.rating,
            comment = recommendedGame.comment
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

        // Login screen.
        composable(FavGameRoutes.LOGIN) {
            LoginScreen(
                onLoginClick = onLoginClick
            )
        }

        // Home screen.
        composable(FavGameRoutes.GAME_LIST) {
            GameListScreen(
                games = displayedGames,
                displayName = displayName,
                profilePhotoUrl = profilePhotoUrl,

                onGameClick = { game ->
                    navController.navigate(
                        "${FavGameRoutes.GAME_DETAILS}/api/${game.id}"
                    )
                },

                onProfileClick = {
                    navController.navigate(
                        FavGameRoutes.SETTINGS
                    ) {
                        launchSingleTop = true
                    }
                },

                onAddClick = {
                    navController.navigate(
                        "${FavGameRoutes.ADD_GAME}/api"
                    )
                },

                onViewAllClick = {
                    navController.navigate(
                        FavGameRoutes.VIEW_ALL_GAMES
                    ) {
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
                        apiViewModel?.deleteGames(
                            accessToken,
                            selectedIds
                        )
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

                onAddClick = {
                    navController.navigate(
                        "${FavGameRoutes.ADD_GAME}/api"
                    )
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
                        navController.navigate(
                            "${FavGameRoutes.GAME_DETAILS}/api/${apiGame.id}"
                        )
                    }
                },

                onRemoveGame = { gameToDelete ->
                    // Same temporary lookup as onGameClick, to get the backend id
                    val apiGame = apiGames.find {
                        it.gameName == gameToDelete.gameName &&
                                it.thumbnail == (gameToDelete.thumbnail ?: "") &&
                                it.gameDescription == gameToDelete.gameDescription &&
                                it.platform == gameToDelete.platform &&
                                it.genre == gameToDelete.genre &&
                                it.rating == gameToDelete.rating &&
                                it.comment == gameToDelete.comment
                    }
                    val accessToken = token

                    // Deletes on the server, then the ViewModel reloads the list
                    if (apiGame != null && accessToken != null) {
                        apiViewModel?.deleteGame(accessToken, apiGame.id)
                    }
                }
            )
        }

        // Game attributes can be opened from multiple screens, so using
        // popBackStack returns to the screen that originally opened it.
        dialog(
            route = "${FavGameRoutes.GAME_DETAILS}/{source}/{gameId}",
            arguments = listOf(
                navArgument("source") {
                    type = NavType.StringType
                },
                navArgument("gameId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val source = entry.arguments
                ?.getString("source")
                ?: "api"

            val gameId = entry.arguments
                ?.getString("gameId")
                .orEmpty()

            val isRecommended = source == "recommended"

            val game = if (isRecommended) {
                displayedRecommendedGames.find {
                    it.id == gameId
                }
            } else {
                displayedGames.find {
                    it.id == gameId
                }
            }

            // Personal games can be edited by the user or an admin.
            // Recommended games can only be edited by an admin.
            val canEdit = !isRecommended || isAdmin

            if (game != null) {
                GameDetailsPopup(
                    game = game,
                    canEdit = canEdit,

                    onChangeRating = { newRating ->
                        val accessToken = token

                        if (accessToken != null && canEdit) {
                            val body: Map<String, Any> = mapOf(
                                "rating" to newRating
                            )

                            if (isRecommended) {
                                recommendedGamesViewModel
                                    ?.updateRecommendation(
                                        accessToken,
                                        gameId,
                                        body
                                    )
                            } else {
                                apiViewModel?.updateGame(
                                    accessToken,
                                    gameId,
                                    body
                                )
                            }
                        }
                    },

                    onChangeComment = { newComment ->
                        val accessToken = token

                        if (accessToken != null && canEdit) {
                            val body: Map<String, Any> = mapOf(
                                "comment" to newComment
                            )

                            if (isRecommended) {
                                recommendedGamesViewModel
                                    ?.updateRecommendation(
                                        accessToken,
                                        gameId,
                                        body
                                    )
                            } else {
                                apiViewModel?.updateGame(
                                    accessToken,
                                    gameId,
                                    body
                                )
                            }
                        }
                    },

                    onChangeAll = {
                        if (canEdit) {
                            navController.navigate(
                                "${FavGameRoutes.CHANGE_ALL_ATTRIBUTES}/$source/$gameId"
                            )
                        }
                    },

                    // Return to the screen that opened the popup.
                    onClose = {
                        navController.popBackStack()
                    }
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

        // Change every attribute using PUT instead of PATCH.
        composable(
            route = "${FavGameRoutes.CHANGE_ALL_ATTRIBUTES}/{source}/{gameId}",
            arguments = listOf(
                navArgument("source") {
                    type = NavType.StringType
                },
                navArgument("gameId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val source = entry.arguments
                ?.getString("source")
                ?: "api"

            val gameId = entry.arguments
                ?.getString("gameId")
                .orEmpty()

            val isRecommended = source == "recommended"

            // Personal games can be edited by the user or an admin.
            // Recommended games can only be edited by an admin.
            val canEdit = !isRecommended || isAdmin

            val game = if (isRecommended) {
                displayedRecommendedGames.find {
                    it.id == gameId
                }
            } else {
                displayedGames.find {
                    it.id == gameId
                }
            }

            when {
                game == null -> {
                    PlaceholderScreen(
                        title = "Game not found",
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                !canEdit -> {
                    PlaceholderScreen(
                        title = "Editing is not available",
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                else -> {
                    ChangeAllAttributesScreen(
                        game = game,

                        // Return to GameAttributesScreen.
                        onBackClick = {
                            navController.popBackStack()
                        },

                        onSaveClick = { changedGame ->
                            val accessToken = token

                            if (accessToken != null) {
                                // PUT sends the complete game body.
                                val body: Map<String, Any> = mapOf(
                                    "gameName" to changedGame.gameName,
                                    "thumbnail" to (
                                            changedGame.thumbnail ?: ""
                                            ),
                                    "gameDescription" to
                                            changedGame.gameDescription,
                                    "platform" to changedGame.platform,
                                    "genre" to changedGame.genre,
                                    "rating" to changedGame.rating,
                                    "comment" to changedGame.comment
                                )

                                if (isRecommended) {
                                    // Only admins can replace all attributes
                                    // of a recommended game.
                                    recommendedGamesViewModel
                                        ?.replaceRecommendation(
                                            accessToken,
                                            gameId,
                                            body
                                        )
                                } else {
                                    // Personal games are replaced in Api.
                                    apiViewModel?.replaceGame(
                                        accessToken,
                                        gameId,
                                        body
                                    )
                                }

                                navController.popBackStack()
                            }
                        }
                    )
                }
            }
        }

        // Settings / Profile.

        // Settings / Profile.
        composable(FavGameRoutes.SETTINGS) {
            SettingsScreen(
                adminUsers = adminUsers,
                selectedAdminUser = selectedAdminUser,
                adminLoading = adminLoading,
                adminError = adminError,

                onLoadAdminUsers = {
                    val accessToken = token

                    if (isAdmin && accessToken != null) {
                        adminViewModel?.clearError()
                        adminViewModel?.loadUsers(accessToken)
                    }
                },

                onViewAdminUser = { userId ->
                    val accessToken = token

                    if (
                        isAdmin &&
                        accessToken != null &&
                        userId.isNotBlank()
                    ) {
                        adminViewModel?.clearError()
                        adminViewModel?.loadUser(
                            accessToken,
                            userId
                        )
                    }
                },

                onDeleteAdminUser = { userId ->
                    val accessToken = token

                    if (
                        isAdmin &&
                        accessToken != null &&
                        userId != user?.id
                    ) {
                        adminViewModel?.clearError()
                        adminViewModel?.deleteUser(
                            accessToken,
                            userId
                        )
                    }
                },

                onToggleAdminUser = { userId, newAdminValue ->
                    val accessToken = token

                    if (
                        isAdmin &&
                        accessToken != null &&
                        userId != user?.id
                    ) {
                        adminViewModel?.clearError()
                        adminViewModel?.toggleAdmin(
                            accessToken,
                            userId,
                            newAdminValue
                        )
                    }
                },

                onSaveIcon = { iconUrl ->
                    if (token != null) {
                        userViewModel?.clearError()
                        userViewModel?.updateIcon(token, iconUrl)
                    }
                },

                onBackClick = {
                    navController.navigate(FavGameRoutes.GAME_LIST) {
                        launchSingleTop = true
                    }
                },

                onLogoutClick = {
                    onLogoutClick()

                    navController.navigate(FavGameRoutes.LOGIN) {
                        popUpTo(FavGameRoutes.GAME_LIST) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },

                isAdmin = isAdmin,
                currentUserId = user?.id,
                displayName = displayName,
                profileImageUrl = profilePhotoUrl,
                loading = userLoading,
                error = userError,
                deleteSucceeded = deleteSucceeded,

                onSaveDisplayName = { newName ->
                    if (token != null) {
                        userViewModel?.clearError()
                        userViewModel?.updateDisplayName(
                            token,
                            newName
                        )
                    }
                },

                onDeleteAccount = {
                    if (token != null) {
                        userViewModel?.clearError()
                        userViewModel?.deleteUser(token)
                    }
                },

                onClearError = {
                    userViewModel?.clearError()
                },

                onAccountDeleted = {
                    // Reset the success flag so it cannot trigger again later.
                    userViewModel?.resetDeleteSucceeded()

                    // Clear the saved token and PKCE verifier.
                    onLogoutClick()

                    // Return to Login and remove Home from the back stack.
                    navController.navigate(FavGameRoutes.LOGIN) {
                        popUpTo(FavGameRoutes.GAME_LIST) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
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
            val target = entry.arguments
                ?.getString("target")
                ?: "api"

            AddGameScreen(
                // AddGame can be opened from multiple locations, so using
                // popBackStack returns to the screen that opened it.
                onBackClick = {
                    navController.popBackStack()
                },

                onSaveClick = { newGame ->
                    val accessToken = token

                    if (accessToken != null) {
                        val body: Map<String, Any> = mapOf(
                            "gameName" to newGame.gameName,
                            "thumbnail" to (
                                    newGame.thumbnail ?: ""
                                    ),
                            "gameDescription" to
                                    newGame.gameDescription,
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
                            apiViewModel?.addGame(
                                accessToken,
                                body
                            )

                            navController.popBackStack()
                        }
                    }
                }
            )
        }

        composable(FavGameRoutes.RECOMMENDED_GAMES) {
            RecommendedGamesScreen(
                games = displayedRecommendedGames,
                isAdmin = isAdmin,
                loading = recommendedLoading,
                error = recommendedError,

                onBackClick = {
                    navController.navigate(FavGameRoutes.GAME_LIST) {
                        launchSingleTop = true
                    }
                },

                onAddClick = {
                    // Only admins can add recommended games.
                    if (isAdmin) {
                        navController.navigate(
                            "${FavGameRoutes.ADD_GAME}/recommended"
                        )
                    }
                },

                onGameClick = { game ->
                    navController.navigate(
                        "${FavGameRoutes.GAME_DETAILS}/recommended/${game.id}"
                    )
                },

                onDeleteGame = { gameId ->
                    val accessToken = token

                    if (isAdmin && accessToken != null) {
                        recommendedGamesViewModel
                            ?.deleteRecommendation(accessToken, gameId)
                    }
                },

                onRetry = {
                    val accessToken = token

                    if (accessToken != null) {
                        recommendedGamesViewModel
                            ?.loadRecommendations(accessToken)
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

        Button(
            onClick = onBack,
            modifier = Modifier.padding(
                top = 16.dp
            )
        ) {
            Text("Back")
        }
    }
}