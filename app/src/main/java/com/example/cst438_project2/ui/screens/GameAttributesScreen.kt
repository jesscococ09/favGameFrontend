
package com.example.cst438_project2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    onClose: () -> Unit
) {
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
                    Text("Close", color = Color.White)
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
                    Text("No cover image", color = Color.White)
                }
            }

            DetailItem("Description", game.description)
            DetailItem("Platform", game.platform)
            DetailItem("Genre", game.genre)

            DetailItem(
                "Rating",
                if (game.rating in 1..10) {
                    "${game.rating}/10"
                } else {
                    "Not rated"
                }
            )

            DetailItem("Comment", game.comment)

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
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
