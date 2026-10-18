package com.example.cst438_project2.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val ChangeAllBackground = Color(0xFF121214)
private val ChangeAllFieldBackground = Color(0xFF1C1C20)
private val ChangeAllPurple = Color(0xFF5A287D)

@Composable
fun ChangeAllAttributesScreen(
    game: Game,
    onBackClick: () -> Unit,
    onSaveClick: (NewGame) -> Unit,
    modifier: Modifier = Modifier
) {
    var gameName by remember(game.id) {
        mutableStateOf(game.title)
    }

    var thumbnail by remember(game.id) {
        mutableStateOf(game.imageUrl)
    }

    var gameDescription by remember(game.id) {
        mutableStateOf(game.description)
    }

    var platform by remember(game.id) {
        mutableStateOf(game.platform)
    }

    var genre by remember(game.id) {
        mutableStateOf(game.genre)
    }

    var rating by remember(game.id) {
        mutableIntStateOf(game.rating)
    }

    var comment by remember(game.id) {
        mutableStateOf(game.comment)
    }

    // Opens the phone's photo picker (skipped in the Android Studio preview, where it can't run).
    val isPreview = LocalInspectionMode.current

    val photoPicker = if (isPreview) {
        null
    } else {
        rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            if (uri != null) {
                thumbnail = uri.toString()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ChangeAllBackground)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        // Header: back arrow and title.
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
                    imageVector =
                        Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Change All Attributes",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // The form scrolls if it does not fit on the screen.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 8.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(20.dp)
        ) {
            ChangeAllThumbnailPicker(
                thumbnail = thumbnail.ifBlank {
                    null
                },
                onClick = {
                    photoPicker?.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts
                                .PickVisualMedia
                                .ImageOnly
                        )
                    )
                }
            )

            ChangeAllTextField(
                label = "Game Name",
                value = gameName,
                onValueChange = {
                    gameName = it
                },
                placeholder = "Enter game name",
                singleLine = true
            )

            ChangeAllTextField(
                label = "Description",
                value = gameDescription,
                onValueChange = {
                    gameDescription = it
                },
                placeholder = "What's the game about?",
                minLines = 3
            )

            ChangeAllDropdown(
                label = "Platform",
                placeholder = "Select a platform",
                options = platformOptions,
                selected = platform,
                onSelect = {
                    platform = it
                }
            )

            ChangeAllDropdown(
                label = "Genre",
                placeholder = "Select a genre",
                options = genreOptions,
                selected = genre,
                onSelect = {
                    genre = it
                }
            )

            ChangeAllDropdown(
                label = "Rating",
                placeholder = "Select a rating (1-10)",
                options = ratingOptions,
                selected = if (rating in 1..10) {
                    rating.toString()
                } else {
                    ""
                },
                onSelect = {
                    rating = it.toInt()
                }
            )

            ChangeAllTextField(
                label = "Comment",
                value = comment,
                onValueChange = {
                    comment = it
                },
                placeholder = "Add a comment",
                minLines = 5
            )

            Button(
                onClick = {
                    onSaveClick(
                        NewGame(
                            gameName = gameName.trim(),
                            thumbnail = thumbnail
                                .trim()
                                .ifBlank { null },
                            gameDescription =
                                gameDescription.trim(),
                            platform = platform,
                            genre = genre,
                            rating = rating,
                            comment = comment.trim()
                        )
                    )
                },
                enabled = gameName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChangeAllPurple,
                    contentColor = Color.White,
                    disabledContainerColor =
                        Color(0xFF2C2C30),
                    disabledContentColor = Color.Gray
                )
            ) {
                Text(
                    text = "Save All Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
    }
}

// Box for the game's picture. Tapping it opens the phone's photo picker.
@Composable
private fun ChangeAllThumbnailPicker(
    thumbnail: String?,
    onClick: () -> Unit
) {
    Column {
        ChangeAllFieldLabel("Picture")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(
                    RoundedCornerShape(16.dp)
                )
                .background(
                    ChangeAllFieldBackground
                )
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!thumbnail.isNullOrBlank()) {
                AsyncImage(
                    model = thumbnail,
                    contentDescription = "Game picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector =
                            Icons.Filled.AddPhotoAlternate,
                        contentDescription = "Change picture",
                        tint = Color.LightGray,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Tap to change picture",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// Small gray label above each field.
@Composable
private fun ChangeAllFieldLabel(
    text: String
) {
    Text(
        text = text,
        color = Color.LightGray,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(
            bottom = 6.dp
        )
    )
}

// Shared dark style for every text field and dropdown.
@Composable
private fun changeAllFieldColors(): TextFieldColors =
    TextFieldDefaults.colors(
        focusedContainerColor =
            ChangeAllFieldBackground,
        unfocusedContainerColor =
            ChangeAllFieldBackground,
        focusedIndicatorColor =
            Color.Transparent,
        unfocusedIndicatorColor =
            Color.Transparent,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = Color.White,
        focusedPlaceholderColor = Color.Gray,
        unfocusedPlaceholderColor = Color.Gray,
        focusedTrailingIconColor =
            Color.LightGray,
        unfocusedTrailingIconColor =
            Color.LightGray
    )

@Composable
private fun ChangeAllTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1,
    singleLine: Boolean = false
) {
    Column {
        ChangeAllFieldLabel(label)

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder)
            },
            singleLine = singleLine,
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            colors = changeAllFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Read-only field that opens a menu of options when tapped.
@Composable
private fun ChangeAllDropdown(
    label: String,
    placeholder: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Column {
        ChangeAllFieldLabel(label)

        Box {
            TextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                placeholder = {
                    Text(placeholder)
                },
                trailingIcon = {
                    Icon(
                        imageVector =
                            Icons.Filled.ArrowDropDown,
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = changeAllFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        expanded = true
                    }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier.background(
                    ChangeAllFieldBackground
                )
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = Color.White
                            )
                        },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}