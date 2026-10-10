package com.example.cst438_project2.ui.screens



import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// One game shown in the list
data class Game(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val updates: Int = 0   // number shown on the badge
)

// Test data until the API is connected
val sampleGames = listOf(
    Game(1, "Minecraft", "https://example.com/minecraft.jpg", updates = 3),
    Game(2, "Elden Ring", "https://example.com/elden-ring.jpg", updates = 1),
    Game(3, "Stardew Valley", "https://example.com/stardew.jpg"),
    Game(4, "Hollow Knight", "https://example.com/hollow-knight.jpg"),
    Game(5, "Celeste", "https://example.com/celeste.jpg")
)



private val ScreenBg = Color(0xFF121214)
private val CardBg = Color(0xFF1C1C20)
private val ImagePlaceholder = Color(0xFF2C2C2A)
private val BadgeColor = Color(0xFF7B83EB)

@Composable
fun GameListScreen(
    games: List<Game>,
    onGameClick: (Game) -> Unit,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = { },
    onAddClick: () -> Unit = { }
) {
    // What the user typed in the search box
    var searchText by remember { mutableStateOf("") }
    // Only show games whose title matches the search
    val shownGames = games.filter { it.title.contains(searchText, ignoreCase = true) }

    Column(
        modifier = modifier.background(ScreenBg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Search bar, plus button, and profile circle (stays at the top, doesn't scroll)
        TopBar(
            searchText = searchText,
            onSearchChange = { searchText = it },
            onProfileClick = onProfileClick,
            onAddClick = onAddClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Games (${shownGames.size})",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            }
            items(shownGames, key = { it.id }) { game ->
                GameCard(game = game, onClick = { onGameClick(game) })
            }
        }
    }
}

@Composable
fun TopBar(
    searchText: String,
    onSearchChange: (String) -> Unit,
    onProfileClick: () -> Unit,
    onAddClick: () -> Unit,
    profilePhotoUrl: String? = null   // set this later once photo upload works
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)   // same width as the cards so the edges line up
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            value = searchText,
            onValueChange = onSearchChange,
            placeholder = { Text("Search games") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg,
                focusedIndicatorColor = Color.Transparent,   // hides the underline
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
                focusedLeadingIconColor = Color.LightGray,
                unfocusedLeadingIconColor = Color.LightGray
            ),
            modifier = Modifier.weight(1f)   // search box takes up the leftover space
        )
        AddButton(onClick = onAddClick)
        ProfileCircle(photoUrl = profilePhotoUrl, onClick = onProfileClick)
    }
}

// Circle with a plus sign
@Composable
fun AddButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(CardBg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.Add,
            contentDescription = "Add",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

// Empty circle for now; shows the user's photo once photoUrl is set
@Composable
fun ProfileCircle(photoUrl: String?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(ImagePlaceholder)
            .border(1.dp, Color.Gray, CircleShape)   // outline so the empty circle is visible
            .clickable(onClick = onClick)
            .semantics { contentDescription = "Profile" }
    ) {
        if (photoUrl != null) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Profile photo",
                contentScale = ContentScale.Crop,   // fills the circle without stretching
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun GameCard(game: Game, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)   // card width: 90% of the screen
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        // Image across the top of the card
        Box {
            AsyncImage(
                model = game.imageUrl,
                contentDescription = game.title,
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(ImagePlaceholder),  // shown while loading
                error = ColorPainter(ImagePlaceholder),        // shown if the URL fails
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)   // image height
            )


        }

        // Title and badge under the image
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 14.dp, end = 28.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = game.title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            // icon button will open game options menu later
            IconButton(onClick = {  }) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = "Game options",
                    tint = Color.LightGray
                )
            }
        }
    }
}


// Lets you see the screen in Android Studio without running the app
@Preview(showBackground = true)
@Composable
fun GameListScreenPreview() {
    GameListScreen(games = sampleGames, onGameClick = { })
}