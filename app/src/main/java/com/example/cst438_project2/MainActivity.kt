package com.example.cst438_project2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cst438_project2.data.AuthStorage
import com.example.cst438_project2.oauth.PKCEUtil
import com.example.cst438_project2.oauth.buildGithubAuthUrl
import com.example.cst438_project2.ui.screens.GameListScreen
import com.example.cst438_project2.ui.screens.LoginScreen
import com.example.cst438_project2.ui.screens.sampleGames

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authStorage= AuthStorage(this)
        val token=authStorage.loadAccessToken()
        enableEdgeToEdge()
        setContent {
            if (token!=null) {
                // TODO: Replace `sampleGames` with real backend data.
                // Someone API call:
                // val api = RetrofitClient.create(token).create(ApiService::class.java)
                // val games = api.listMyGames(token, page = 0, size = 4)
                // Then pass `games` into GameListScreen.
                GameListScreen(
                    // temporary placeholder
                    games = sampleGames,
                    //TODO:add other navigates
                    onGameClick = { /* TODO: navigate to GameDetail */ },
                    onProfileClick = { /* TODO: navigate to Profile */ },
                    onAddClick = { /* TODO: navigate to AddGame */ }
                )
            } else {
                LoginScreen(
                    onLoginClick = {
                        // 1.PKCE verifier
                        val verifier = PKCEUtil.generateCodeVerifier()
                        authStorage.saveCodeVerifier(verifier)
                        // 2.Challenge
                        val challenge = PKCEUtil.generateCodeChallenge(verifier)
                        // 3.GitHub URL
                        val url = buildGithubAuthUrl(challenge)
                        // 4.Launch
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    }
                )
            }
        }
    }
}