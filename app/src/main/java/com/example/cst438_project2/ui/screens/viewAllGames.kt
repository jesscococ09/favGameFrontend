package com.example.cst438_project2.ui.screens

// these two imports are for the filter icon
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.filled.Sort

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Same dark palette as the other screens
private val ViewAllBg = Color(0xFF121214)
private val ViewAllDivider = Color(0xFF2C2C30)
private val ViewAllSubtle = Color(0xFFB0B0B8)

/**
 * View All Games screen: every game the user has added with the Add Game screen,
 * shown as a simple list with the title on the left and the genre on the right.
 *
 * Takes the list as a parameter and defaults to empty, since no games are
 * saved yet. Once games are stored (ViewModel / backend), pass that list in.
 */
@Composable
fun ViewAllGamesScreen(
    games: List<NewGame> = emptyList(),
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onGameClick: (NewGame) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ViewAllBg)
    ) {
        // Header: back arrow and title, same style as Settings and Add Game
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "All Games",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }


        // Sort button (does nothing yet; sorting gets added later)
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = "Sort",
                tint = ViewAllSubtle,
                modifier = Modifier.size(22.dp)
            )
        }

        if (games.isEmpty()) {
            // Nothing added yet
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "You haven't added any games yet",
                    color = ViewAllSubtle,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Small count line, e.g. "3 games"
            Text(
                text = if (games.size == 1) "1 game" else "${games.size} games",
                color = ViewAllSubtle,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            // Scrollable list. NewGame has no id yet, so rows are keyed by position.
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(games) { _, game ->
                    ViewAllGameRow(game = game, onClick = { onGameClick(game) })
                    HorizontalDivider(
                        color = ViewAllDivider,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }
    }
}

/**
 * One row: title on the left, genre on the right.
 * A long title is cut off with "…" so it never pushes the genre off screen.
 */

@Composable
private fun ViewAllGameRow(game: NewGame, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = game.gameName,
            color = Color.White,
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f) // takes the space the genre doesn't need
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            // Genre is optional on the Add Game screen, so show a dash if it was skipped
            text = game.genre.ifBlank { "—" },
            color = ViewAllSubtle,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true, name = "Empty (default)")
@Composable
private fun ViewAllGamesEmptyPreview() {
    ViewAllGamesScreen()
}

@Preview(showBackground = true, name = "With games")
@Composable
private fun ViewAllGamesPreview() {
    ViewAllGamesScreen(
        games = listOf(
            NewGame("Halo", null, "", "PC", "Shooter", 9, ""),
            NewGame("Stardew Valley", null, "", "PC", "Simulation", 10, ""),
            NewGame("The Legend of Zelda: Tears of the Kingdom", null, "", "Nintendo Switch", "Adventure", 0, ""),
            NewGame("Untitled Game", null, "", "", "", 0, "")
        )
    )
}