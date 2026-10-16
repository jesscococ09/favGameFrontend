package com.example.cst438_project2.ui.screens

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

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

private val ViewAllField = Color(0xFF1C1C20)
private val ViewAllAccent = Color(0xFF7B83EB)


/**
 * The filters the user picked. null / blank means "don't filter on this".
 */
data class GameFilters(
    val genre: String? = null,
    val platform: String? = null,
    val minRating: Int? = null,
    val commentQuery: String = ""
) {
    // True when at least one filter is turned on
    val isActive: Boolean
        get() = genre != null || platform != null || minRating != null || commentQuery.isNotBlank()
}

/**
 * Returns only the games that pass every active filter.
 * Outside the composable so it can be unit tested without an emulator.
 */
fun filterGames(games: List<NewGame>, filters: GameFilters): List<NewGame> =
    games.filter { game ->
        (filters.genre == null || game.genre == filters.genre) &&
                (filters.platform == null || game.platform == filters.platform) &&
                (filters.minRating == null || game.rating >= filters.minRating) &&
                (filters.commentQuery.isBlank() ||
                        game.comment.contains(filters.commentQuery.trim(), ignoreCase = true))
    }

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

    // Current filters, and whether the filter panel is showing
    var filters by remember { mutableStateOf(GameFilters()) }
    var showFilters by remember { mutableStateOf(false) }

    // Re-filter only when the list or the filters change
    val shownGames = remember(games, filters) { filterGames(games, filters) }

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


            // Filter button right after the title. Turns purple while any filter is on.
            IconButton(onClick = { showFilters = !showFilters }) {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = "Filter",
                    tint = if (filters.isActive) ViewAllAccent else ViewAllSubtle,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        if (showFilters) {
            FilterPanel(filters = filters, onFiltersChange = { filters = it })
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
        } else if (shownGames.isEmpty()) {
            // Games exist, but none pass the filters
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "No games match your filters",
                    color = ViewAllSubtle,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Count line, e.g. "3 games", or "2 of 5 games" while filtering
            val countText = when {
                filters.isActive -> "${shownGames.size} of ${games.size} games"
                games.size == 1 -> "1 game"
                else -> "${games.size} games"
            }
            Text(
                text = countText,
                color = ViewAllSubtle,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            // Scrollable list of the filtered games
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(shownGames) { _, game ->
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
 * Filter controls: genre, platform and rating dropdowns, a comment
 * search box, and a button to clear everything.
 */

@Composable
private fun FilterPanel(
    filters: GameFilters,
    onFiltersChange: (GameFilters) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterDropdown(
                label = "Genre",
                selected = filters.genre,
                options = genreOptions,          // same list as the Add Game screen
                onSelect = { onFiltersChange(filters.copy(genre = it)) },
                modifier = Modifier.weight(1f)
            )
            FilterDropdown(
                label = "Platform",
                selected = filters.platform,
                options = platformOptions,       // same list as the Add Game screen
                onSelect = { onFiltersChange(filters.copy(platform = it)) },
                modifier = Modifier.weight(1f)
            )
        }

        FilterDropdown(
            label = "Rating",
            // Shown as "7+" so it's clear it means "7 or higher"
            selected = filters.minRating?.let { "$it+" },
            options = (1..10).map { "$it+" },
            onSelect = { choice ->
                onFiltersChange(filters.copy(minRating = choice?.removeSuffix("+")?.toInt()))
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = filters.commentQuery,
            onValueChange = { onFiltersChange(filters.copy(commentQuery = it)) },
            placeholder = { Text("Comment contains…", color = ViewAllSubtle) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = ViewAllField,
                unfocusedContainerColor = ViewAllField,
                focusedBorderColor = ViewAllAccent,
                unfocusedBorderColor = ViewAllDivider
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Only show "Clear filters" when something is set
        if (filters.isActive) {
            TextButton(onClick = { onFiltersChange(GameFilters()) }) {
                Text("Clear filters", color = ViewAllAccent)
            }
        }
    }
}

/**
 * A small box showing the current choice (or "All") that opens a menu of options.
 * Picking "All" sends null, which means "don't filter on this".
 */
@Composable
private fun FilterDropdown(
    label: String,
    selected: String?,
    options: List<String>,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ViewAllField)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .semantics { contentDescription = "$label filter" },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label: ${selected ?: "All"}",
                color = if (selected != null) Color.White else ViewAllSubtle,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = ViewAllSubtle)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("All") },
                onClick = {
                    onSelect(null)
                    expanded = false
                }
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
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
            NewGame(
                "The Legend of Zelda: Tears of the Kingdom",
                null,
                "",
                "Nintendo Switch",
                "Adventure",
                0,
                ""
            ),
            NewGame("Untitled Game", null, "", "", "", 0, "")
        )
    )
}