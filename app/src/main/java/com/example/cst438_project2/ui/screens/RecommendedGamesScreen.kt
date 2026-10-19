package com.example.cst438_project2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val RecommendedBackground = Color(0xFF121214)
private val RecommendedSurface = Color(0xFF1C1C20)
private val RecommendedPurple = Color(0xFF5A287D)
private val RecommendedDanger = Color(0xFFB3261E)

/**
 * Returns up to four randomly selected games.
 * If fewer than four games exist, returns all available games.
 */
fun selectRecommendedGames(
    games: List<Game>,
    limit: Int = 4
): List<Game> {
    return games.shuffled().take(limit.coerceAtLeast(0))
}

/**
 * Displays recommended games.
 *
 * Admins can remove recommendations from the shared list.
 * Removing a recommendation should not delete it from a user's
 * personal game library.
 */
@Composable
fun RecommendedGamesScreen(
    games: List<Game>,
    isAdmin: Boolean,
    loading: Boolean,
    error: String?,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onGameClick: (Game) -> Unit,
    onDeleteGame: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Select up to four games, randomizing when the input list changes.
    val shownGames = remember(games) {
        selectRecommendedGames(games)
    }

    // Tracks which game the admin wants to remove.
    var gamePendingDelete by remember {
        mutableStateOf<Game?>(null)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RecommendedBackground)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {

        // Screen header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Recommended Games",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Discover your next favorite",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
            }

            // Only admins see the Add button.
            if (isAdmin) {
                IconButton(
                    onClick = onAddClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add recommended game",
                        tint = Color.White
                    )
                }
            }
        }

        // Loading, error, empty, or game-list content
        when {
            loading && games.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = RecommendedPurple
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Loading recommendations…",
                        color = Color.LightGray
                    )
                }
            }

            error != null && games.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Couldn't load recommendations.",
                        color = Color.White
                    )

                    Text(
                        text = error,
                        color = Color.LightGray,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Button(
                        onClick = onRetry,
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text("Try again")
                    }
                }
            }

            shownGames.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No recommendations yet.",
                        color = Color.White,
                        fontSize = 18.sp
                    )

                    Text(
                        text = if (isAdmin) {
                            "Add a game to start the list."
                        } else {
                            "Check back later for new games."
                        },
                        color = Color.LightGray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            else -> {
                if (error != null) {
                    Text(
                        text = "Could not refresh recommendations: $error",
                        color = Color(0xFFFFB4AB),
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 4.dp
                        )
                    )
                }

                Text(
                    text = "A random selection of ${shownGames.size} game(s)",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 4.dp
                    )
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = shownGames,
                        key = { it.id }
                    ) { game ->

                        // Individual recommendation card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(
                                    RoundedCornerShape(16.dp)
                                )
                                .background(RecommendedSurface)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // Game cover image
                            AsyncImage(
                                model = game.imageUrl
                                    .takeIf { it.isNotBlank() },
                                contentDescription = "${game.title} cover",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(
                                        width = 94.dp,
                                        height = 118.dp
                                    )
                                    .clip(
                                        RoundedCornerShape(10.dp)
                                    )
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp)
                            ) {

                                // Game title
                                Text(
                                    text = game.title,
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                // Genre and platform
                                Text(
                                    text = "${game.genre} • ${game.platform}",
                                    color = Color(0xFFC8B5DC),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                // Game description
                                Text(
                                    text = game.description.ifBlank {
                                        "No description available."
                                    },
                                    color = Color.LightGray,
                                    fontSize = 13.sp,
                                    maxLines = 3,
                                    modifier = Modifier.padding(top = 6.dp)
                                )

                                // Open game details
                                TextButton(
                                    onClick = {
                                        onGameClick(game)
                                    },
                                    contentPadding = PaddingValues(
                                        horizontal = 0.dp,
                                        vertical = 2.dp
                                    )
                                ) {
                                    Text(
                                        text = "View details",
                                        color = Color(0xFFBFA0E2)
                                    )
                                }

                                // Only admins see the Remove button.
                                if (isAdmin) {
                                    OutlinedButton(
                                        onClick = {
                                            gamePendingDelete = game
                                        },
                                        colors = ButtonDefaults
                                            .outlinedButtonColors(
                                                contentColor =
                                                    Color(0xFFFFB4AB)
                                            ),
                                        contentPadding = PaddingValues(
                                            horizontal = 10.dp,
                                            vertical = 0.dp
                                        )
                                    ) {
                                        Icon(
                                            imageVector =
                                                Icons.Default.Delete,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )

                                        Spacer(
                                            modifier = Modifier.width(5.dp)
                                        )

                                        Text("Remove")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog before deleting a recommendation
    if (isAdmin) {
        gamePendingDelete?.let { game ->
            AlertDialog(
                onDismissRequest = {
                    gamePendingDelete = null
                },

                title = {
                    Text("Remove recommendation?")
                },

                text = {
                    Text(
                        "Remove “${game.title}” from the shared " +
                                "recommended games list? This will not delete " +
                                "it from users’ personal libraries."
                    )
                },

                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteGame(game.id)
                            gamePendingDelete = null
                        }
                    ) {
                        Text(
                            text = "Confirm removal",
                            color = RecommendedDanger
                        )
                    }
                },

                dismissButton = {
                    TextButton(
                        onClick = {
                            gamePendingDelete = null
                        }
                    ) {
                        Text("Cancel")
                    }
                },

                containerColor = RecommendedSurface,
                titleContentColor = Color.White,
                textContentColor = Color.LightGray
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecommendedGamesScreenPreview() {
    val sampleGames = listOf(
        Game(
            id = "1",
            title = "Elden Ring",
            imageUrl = "",
            genre = "Action RPG",
            platform = "PC / PlayStation",
            description = "Explore a vast fantasy world filled with challenging enemies and secrets."
        ),
        Game(
            id = "2",
            title = "Stardew Valley",
            imageUrl = "",
            genre = "Simulation",
            platform = "PC / Nintendo Switch",
            description = "Build your farm, meet the townspeople, and discover the valley."
        ),
        Game(
            id = "3",
            title = "Minecraft",
            imageUrl = "",
            genre = "Sandbox",
            platform = "PC / Console",
            description = "Build, explore, and survive in a world made of blocks."
        ),
        Game(
            id = "4",
            title = "Hollow Knight",
            imageUrl = "",
            genre = "Metroidvania",
            platform = "PC / Console",
            description = "Explore a mysterious underground kingdom in this challenging adventure."
        )
    )

    RecommendedGamesScreen(
        games = sampleGames,
        isAdmin = true,
        loading = false,
        error = null,
        onBackClick = {},
        onAddClick = {},
        onGameClick = {},
        onDeleteGame = {},
        onRetry = {}
    )
}