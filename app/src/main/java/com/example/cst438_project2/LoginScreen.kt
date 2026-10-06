package com.example.cst438_project2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        // Center everything vertically
        verticalArrangement = Arrangement.Center,
        // Center everything horizontally
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App name (subject to change, since I don't think we've agreed on a name yet)
        Text(
            text = "FavGame",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Explain what the user needs to do
        Text(
            text = "Log in to manage your favorite games"
        )
        Spacer(modifier = Modifier.height(32.dp))
        // We'll connect this button to GitHub OAuth later
        Button(
            onClick = onLoginClick
        ) {
            Text("Login with GitHub")
        }
    }
}
