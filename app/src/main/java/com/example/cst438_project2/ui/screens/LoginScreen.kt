package com.example.cst438_project2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colors
private val BackgroundColor = Color(0xFF121214) // Almost black

private val SurfaceColor = Color(0xFF1C1C20) // Dark gray for UI elements

private val DeepPurple = Color(0xFF381057) // For accents

private val ButtonPurple = Color(0xFF5A287D) // Slightly brighter purple for the button

private val PrimaryText = Color(0xFFF1EEF5) // All colors could be assigned to as a theme

private val SecondaryText = Color(0xFFAAA5B0) // So that way, it's easier to code/change later


@Composable
fun LoginScreen(
    onLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "FAVGAME", // Subject to change
                color = PrimaryText,
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            // Small purple accent underneath the title.
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.18f)
                    .height(4.dp)
                    .background(
                        color = DeepPurple,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
            Spacer(
                modifier = Modifier.height(24.dp)
            )
            Text(
                text = "YOUR GAMES. YOUR LIBRARY.",
                color = SecondaryText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.5.sp
            )
            Spacer(
                modifier = Modifier.height(48.dp)
            )
            // Login card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = SurfaceColor,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome back",
                    color = PrimaryText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                Text(
                    text = "Sign in with GitHub to access your game library.",
                    color = SecondaryText,
                    fontSize = 14.sp
                )
                Spacer(
                    modifier = Modifier.height(24.dp)
                )
                // GitHub login button
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonPurple,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Login with GitHub",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
