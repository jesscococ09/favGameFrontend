package com.example.cst438_project2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val DetailsBackground = Color(0xFF1C1C20)
private val DetailsPurple = Color(0xFF5A287D)

// Content displayed inside the navigation dialog.
@Composable
fun GameDetailsPopup(
    game: Game,
    canEdit: Boolean = true,
    onChangeRating: (Int) -> Unit = {},
    onChangeComment: (String) -> Unit = {},
    onChangeAll: () -> Unit = {},
    onClose: () -> Unit
) {
    var showRatingDialog by remember {
        mutableStateOf(false)
    }

    var showCommentDialog by remember {
        mutableStateOf(false)
    }

    // Keeps the rating and comment visibly updated while the popup stays open.
    var displayedRating by remember(game.id) {
        mutableIntStateOf(game.rating)
    }

    var displayedComment by remember(game.id) {
        mutableStateOf(game.comment)
    }

    // Keeps the local values synchronized after the backend finishes updating.
    LaunchedEffect(game.rating, game.comment) {
        displayedRating = game.rating
        displayedComment = game.comment
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 420.dp)
            .heightIn(max = 650.dp),
        shape = RoundedCornerShape(24.dp),
        color = DetailsBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Title and close button.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = game.title,
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onClose) {
                    Text(
                        text = "Close",
                        color = Color.White
                    )
                }
            }

            // Game cover.
            if (game.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = game.imageUrl,
                    contentDescription = "${game.title} cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            DetailsPurple,
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No cover image",
                        color = Color.White
                    )
                }
            }

            DetailItem(
                label = "Description",
                value = game.description
            )

            DetailItem(
                label = "Platform",
                value = game.platform
            )

            DetailItem(
                label = "Genre",
                value = game.genre
            )

            EditableDetailItem(
                label = "Rating",
                value = if (displayedRating in 1..10) {
                    "$displayedRating/10"
                } else {
                    "Not rated"
                },
                canEdit = canEdit,
                onChangeClick = {
                    showRatingDialog = true
                }
            )

            EditableDetailItem(
                label = "Comment",
                value = displayedComment.ifBlank {
                    "No comment"
                },
                canEdit = canEdit,
                onChangeClick = {
                    showCommentDialog = true
                }
            )

            // Personal games can be edited by their owner, recommended games only by admin.
            if (canEdit) {
                Button(
                    onClick = onChangeAll,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DetailsPurple
                    )
                ) {
                    Text("Change All Attributes")
                }
            }

            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DetailsPurple
                )
            ) {
                Text("Back to My Games")
            }
        }
    }

    // Small popup used to change only the rating.
    if (showRatingDialog) {
        ChangeRatingDialog(
            currentRating = displayedRating,
            onDismiss = {
                showRatingDialog = false
            },
            onSave = { newRating ->
                // Update the visible value immediately, then send the PATCH.
                displayedRating = newRating
                onChangeRating(newRating)
                showRatingDialog = false
            }
        )
    }

    // Small popup used to change only the comment.
    if (showCommentDialog) {
        ChangeCommentDialog(
            currentComment = displayedComment,
            onDismiss = {
                showCommentDialog = false
            },
            onSave = { newComment ->
                // Update the visible value immediately, then send the PATCH.
                displayedComment = newComment
                onChangeComment(newComment)
                showCommentDialog = false
            }
        )
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = Color.LightGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun EditableDetailItem(
    label: String,
    value: String,
    canEdit: Boolean,
    onChangeClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.LightGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            if (canEdit) {
                TextButton(
                    onClick = onChangeClick,
                    contentPadding = PaddingValues(
                        horizontal = 8.dp,
                        vertical = 0.dp
                    )
                ) {
                    Text(
                        text = "Change",
                        color = DetailsPurple
                    )
                }
            }
        }

        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

// Lets the user choose a new rating from the same 1 to 10 range used by Add Game.
@Composable
private fun ChangeRatingDialog(
    currentRating: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var selectedRating by remember(currentRating) {
        mutableIntStateOf(
            if (currentRating in 1..10) {
                currentRating
            } else {
                1
            }
        )
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Change Rating")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Select a rating from 1 to 10.")

                Box {
                    OutlinedButton(
                        onClick = {
                            expanded = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("$selectedRating/10")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        (1..10).forEach { rating ->
                            DropdownMenuItem(
                                text = {
                                    Text("$rating/10")
                                },
                                onClick = {
                                    selectedRating = rating
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(selectedRating)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

// Lets the user replace the current comment.
@Composable
private fun ChangeCommentDialog(
    currentComment: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var comment by remember(currentComment) {
        mutableStateOf(currentComment)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Change Comment")
        },
        text = {
            OutlinedTextField(
                value = comment,
                onValueChange = {
                    comment = it
                },
                label = {
                    Text("Comment")
                },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(comment)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}