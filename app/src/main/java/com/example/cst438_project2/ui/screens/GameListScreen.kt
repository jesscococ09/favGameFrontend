package com.example.cst438_project2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class Game(
    val id: String,
    val title: String,
    val imageUrl: String,
    val updates: Int = 0,
    val description: String = "No description available yet.",
    val platform: String = "Not specified",
    val genre: String = "Not specified",
    val rating: Int = 0,
    val comment: String = "No comment yet."
)

private val ScreenBg = Color(0xFF121214)
private val CardBg = Color(0xFF1C1C20)
private val ImagePlaceholder = Color(0xFF2C2C2A)
private val LinkPurple = Color(0xFF9A72C4)
private val SubmitBlue = Color(0xFF4E82D9)
private val DeleteRed = Color(0xFFE05252)

@Composable
fun GameListScreen(
    games: List<Game>,
    displayName: String,
    onGameClick: (Game) -> Unit,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onRecommendedClick: () -> Unit = {},
    onDeleteGames: (Set<String>) -> Unit = {},
    profilePhotoUrl: String? = null
) {
    val shownGames = games.take(4)

    var deleteMode by remember {
        mutableStateOf(false)
    }

    var selectedGameIds by remember {
        mutableStateOf<Set<String>>(emptySet())
    }

    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.height(24.dp)
        )

        HomeTopBar(
            displayName = displayName,
            onProfileClick = onProfileClick,
            onAddClick = onAddClick,
            profilePhotoUrl = profilePhotoUrl
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Games",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (deleteMode) {
                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        TextButton(
                            onClick = {
                                showDeleteConfirmation = true
                            },
                            enabled = selectedGameIds.isNotEmpty(),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = SubmitBlue,
                                disabledContentColor = Color.Gray
                            )
                        ) {
                            Text(
                                text = "Submit",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            deleteMode = !deleteMode

                            if (!deleteMode) {
                                selectedGameIds = emptySet()
                            }
                        },
                        enabled = shownGames.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = if (deleteMode) {
                                "Cancel game deletion"
                            } else {
                                "Delete games"
                            },
                            tint = if (shownGames.isEmpty()) {
                                Color.Gray
                            } else {
                                DeleteRed
                            }
                        )
                    }
                }
            }

            if (shownGames.isEmpty()) {
                item {
                    Text(
                        text = "You haven't added any games yet",
                        color = Color.Gray,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 24.dp)
                    )
                }
            } else {
                items(
                    items = shownGames,
                    key = { it.id }
                ) { game ->
                    val isSelected =
                        game.id in selectedGameIds

                    GameCard(
                        game = game,
                        selectionMode = deleteMode,
                        isSelected = isSelected,

                        onSelectionChange = { selected ->
                            selectedGameIds =
                                if (selected) {
                                    selectedGameIds + game.id
                                } else {
                                    selectedGameIds - game.id
                                }
                        },

                        onClick = {
                            if (deleteMode) {
                                selectedGameIds =
                                    if (isSelected) {
                                        selectedGameIds - game.id
                                    } else {
                                        selectedGameIds + game.id
                                    }
                            } else {
                                onGameClick(game)
                            }
                        }
                    )
                }
            }

            item {
                HorizontalDivider(
                    color = Color(0xFF2C2C30),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(top = 8.dp)
                )
            }

            if (!deleteMode) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .clickable(onClick = onViewAllClick)
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View All Games",
                            color = LinkPurple,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clickable(
                            enabled = !deleteMode,
                            onClick = onRecommendedClick
                        )
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recommended Games",
                        color = if (deleteMode) {
                            Color.Gray
                        } else {
                            LinkPurple
                        },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
            },
            title = {
                Text("Delete Selected Games?")
            },
            text = {
                Text(
                    text = if (selectedGameIds.size == 1) {
                        "Are you sure you want to delete the selected game?"
                    } else {
                        "Are you sure you want to delete " +
                                "${selectedGameIds.size} selected games?"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteGames(selectedGameIds)

                        showDeleteConfirmation = false
                        selectedGameIds = emptySet()
                        deleteMode = false
                    }
                ) {
                    Text(
                        text = "Yes",
                        color = DeleteRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("No")
                }
            },
            containerColor = CardBg,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray
        )
    }
}

@Composable
fun HomeTopBar(
    displayName: String,
    onProfileClick: () -> Unit,
    onAddClick: () -> Unit,
    profilePhotoUrl: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(
                top = 20.dp,
                bottom = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (displayName.isBlank()) {
                "Welcome!"
            } else {
                "Welcome, $displayName!"
            },
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        AddButton(
            onClick = onAddClick
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        ProfileCircle(
            photoUrl = profilePhotoUrl,
            onClick = onProfileClick
        )
    }
}

@Composable
fun AddButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(CardBg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add game",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ProfileCircle(
    photoUrl: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(ImagePlaceholder)
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "Profile"
            }
    ) {
        if (!photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun GameCard(
    game: Game,
    selectionMode: Boolean,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBg
        )
    ) {
        AsyncImage(
            model = game.imageUrl,
            contentDescription = game.title,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(ImagePlaceholder),
            error = ColorPainter(ImagePlaceholder),
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 14.dp,
                    end = 8.dp,
                    bottom = 14.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = game.title,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = if (game.rating in 1..10) {
                        "Rating: ${game.rating}/10"
                    } else {
                        "Not rated"
                    },
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }

            if (selectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onSelectionChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = SubmitBlue,
                        uncheckedColor = Color.LightGray,
                        checkmarkColor = Color.White
                    )
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF121214
)
@Composable
fun GameListScreenPreview() {
    val previewGames = listOf(
        Game(
            id = "preview-1",
            title = "Minecraft",
            imageUrl = "",
            description = "Preview description",
            platform = "PC",
            genre = "Sandbox",
            rating = 9,
            comment = "Preview comment"
        ),
        Game(
            id = "preview-2",
            title = "Elden Ring",
            imageUrl = "",
            description = "Preview description",
            platform = "PC",
            genre = "RPG",
            rating = 10,
            comment = "Preview comment"
        ),
        Game(
            id = "preview-3",
            title = "Stardew Valley",
            imageUrl = "",
            description = "Preview description",
            platform = "PC",
            genre = "Simulation",
            rating = 8,
            comment = "Preview comment"
        ),
        Game(
            id = "preview-4",
            title = "Hollow Knight",
            imageUrl = "",
            description = "Preview description",
            platform = "PC",
            genre = "Metroidvania",
            rating = 9,
            comment = "Preview comment"
        )
    )

    GameListScreen(
        games = previewGames,
        displayName = "Preview User",
        onGameClick = {},
        onProfileClick = {},
        onAddClick = {},
        onViewAllClick = {},
        onRecommendedClick = {},
        onDeleteGames = {},
        profilePhotoUrl = null
    )
}