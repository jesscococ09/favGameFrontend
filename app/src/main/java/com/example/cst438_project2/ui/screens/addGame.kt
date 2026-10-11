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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage




data class NewGame(
    val gameName: String,
    val thumbnail: String?,   // picked photo's Uri as a String, or null if none
    val gameDescription: String,
    val platform: String,
    val genre: String,
    val rating: Int,          // 0 = not rated, otherwise 1 to 10
    val comment: String
)

// list of consoles for the platform option in the ui
val platformOptions = listOf(
    "PC", "PlayStation 5", "Xbox Series X|S", "Nintendo Switch", "Mobile"
)
// list of genres
val genreOptions = listOf(
    "Action", "Adventure", "RPG", "Platformer", "Puzzle",
    "Shooter", "Simulation", "Sports", "Strategy"
)
// enables rating value that fits the api
val ratingOptions = (1..10).map { it.toString() }   // "1" to "10", the range the API allows


// makes the screen same color as home screen
private val AddScreenBg = Color(0xFF121214)
private val AddFieldBg = Color(0xFF1C1C20)
private val AddButtonPurple = Color(0xFF5A287D)



@Composable
fun AddGameScreen(
    onBackClick: () -> Unit,
    onSaveClick: (NewGame) -> Unit,
    modifier: Modifier = Modifier
) {
    // What the user has entered so far
    var gameName by remember { mutableStateOf("") }
    var thumbnail by remember { mutableStateOf<String?>(null) }
    var gameDescription by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }

    // Opens the phone's photo picker (skipped in the Android Studio preview, where it can't run)
    val isPreview = LocalInspectionMode.current
    val photoPicker = if (isPreview) null else rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) thumbnail = uri.toString()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AddScreenBg)
    ) {
        // Header: back arrow + big bold title (stays at the top, doesn't scroll)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Add Game",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }



        // The form (scrolls if it doesn't fit)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ThumbnailPicker(
                thumbnail = thumbnail,
                onClick = {
                    photoPicker?.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            FormTextField(
                label = "Game Name",
                value = gameName,
                onValueChange = { gameName = it },
                placeholder = "Enter game name",
                singleLine = true
            )


            FormTextField(
                label = "Description",
                value = gameDescription,
                onValueChange = { gameDescription = it },
                placeholder = "What's the game about?",
                minLines = 3
            )

            DropdownField(
                label = "Platform",
                placeholder = "Select a platform",
                options = platformOptions,
                selected = platform,
                onSelect = { platform = it }
            )

            DropdownField(
                label = "Genre",
                placeholder = "Select a genre",
                options = genreOptions,
                selected = genre,
                onSelect = { genre = it }
            )

            DropdownField(
                label = "Rating",
                placeholder = "Select a rating (1-10)",
                options = ratingOptions,
                selected = if (rating == 0) "" else rating.toString(),   // empty until one is picked
                onSelect = { rating = it.toInt() }
            )

            FormTextField(
                label = "Comment",
                value = comment,
                onValueChange = { comment = it },
                placeholder = "Add a comment (optional)",
                minLines = 6   // large box for longer comments
            )

            // Greyed out until a game name is typed (spaces alone don't count)
            Button(
                onClick = {
                    onSaveClick(
                        NewGame(gameName, thumbnail, gameDescription, platform, genre, rating, comment)
                    )
                },
                enabled = gameName.isNotBlank(),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AddButtonPurple,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF2C2C30),
                    disabledContentColor = Color.Gray

                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Add to My Games", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// Small gray label above each field
@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = Color.LightGray,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

// Shared dark style for every text field and dropdown
@Composable
private fun addFieldColors(): TextFieldColors = TextFieldDefaults.colors(
    focusedContainerColor = AddFieldBg,
    unfocusedContainerColor = AddFieldBg,
    focusedIndicatorColor = Color.Transparent,   // hides the underline
    unfocusedIndicatorColor = Color.Transparent,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Color.White,
    focusedPlaceholderColor = Color.Gray,
    unfocusedPlaceholderColor = Color.Gray,
    focusedTrailingIconColor = Color.LightGray,
    unfocusedTrailingIconColor = Color.LightGray
)

// Box for the game's picture: shows a "+" photo icon until a picture is chosen
@Composable
private fun ThumbnailPicker(thumbnail: String?, onClick: () -> Unit) {
    Column {
        FieldLabel("Picture")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)   // same height as the image on the home screen cards
                .clip(RoundedCornerShape(16.dp))
                .background(AddFieldBg)
                .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (thumbnail != null) {
                AsyncImage(
                    model = thumbnail,
                    contentDescription = "Game picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.AddPhotoAlternate,
                        contentDescription = "Add picture",
                        tint = Color.LightGray,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Tap to add a picture", color = Color.Gray, fontSize = 14.sp)
                }
            }
        }
    }
}

// Read-only field that opens a menu of options when tapped
@Composable
private fun DropdownField(
    label: String,
    placeholder: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        FieldLabel(label)
        Box {
            TextField(
                value = selected,
                onValueChange = { },   // user picks from the menu instead of typing
                readOnly = true,
                placeholder = { Text(placeholder) },
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = addFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            // Invisible layer over the field so tapping anywhere on it opens the menu
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { expanded = true }
                    .semantics { contentDescription = "$label dropdown" }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(AddFieldBg)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = Color.White) },
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

// Plain text box with a label (used for game name, description, and comment)
@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1,
    singleLine: Boolean = false
) {
    Column {
        FieldLabel(label)
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            singleLine = singleLine,
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            colors = addFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Lets you see the screen in Android Studio without running the app
@Preview(showBackground = true, heightDp = 1100)

@Composable
fun AddGameScreenPreview() {
    AddGameScreen(onBackClick = { }, onSaveClick = { })
}
