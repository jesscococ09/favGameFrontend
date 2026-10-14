package com.example.cst438_project2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cst438_project2.data.AuthStorage
import com.example.cst438_project2.navigation.AppNavGraph
import com.example.cst438_project2.oauth.PKCEUtil
import com.example.cst438_project2.oauth.buildGithubAuthUrl

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load any previously saved access token.
        val authStorage = AuthStorage(this)
        val token = authStorage.loadAccessToken()

        enableEdgeToEdge()

        setContent {
            AppNavGraph(
                // The initial destination depends on whether a token exists.
                isLoggedIn = token != null,
                // Preserve the existing GitHub OAuth / PKCE launch.
                onLoginClick = {
                    // 1. Generate and save the PKCE verifier.
                    val verifier = PKCEUtil.generateCodeVerifier()
                    authStorage.saveCodeVerifier(verifier)
                    // 2. Generate the challenge associated with that verifier.
                    val challenge =
                        PKCEUtil.generateCodeChallenge(verifier)
                    // 3. Build the GitHub authorization URL.
                    val url = buildGithubAuthUrl(challenge)
                    // 4. Open the authorization page in the browser.
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url)
                    )
                    startActivity(intent)
                },

                // Clear the saved access token and code verifier.
                { authStorage.clear() }
            )
        }
    }
}