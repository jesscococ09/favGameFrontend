package com.example.cst438_project2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cst438_project2.ui.screens.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginScreen(
                onLoginClick = {
                    // TODO:
                    // Finish wiring it up, once the API is connected.
                    // For now, just confirm that the button works.
                }
            )
        }
    }
}