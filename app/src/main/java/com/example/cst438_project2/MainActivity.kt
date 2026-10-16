package com.example.cst438_project2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cst438_project2.data.AuthStorage
import com.example.cst438_project2.data.api.AdminService
import com.example.cst438_project2.data.api.ApiService
import com.example.cst438_project2.data.api.RecommendedGamesService
import com.example.cst438_project2.data.api.RetrofitClient
import com.example.cst438_project2.data.api.UserService
import com.example.cst438_project2.data.repository.AdminRepository
import com.example.cst438_project2.data.repository.ApiRepository
import com.example.cst438_project2.data.repository.RecommendedGamesRepository
import com.example.cst438_project2.data.repository.UserRepository
import com.example.cst438_project2.navigation.AppNavGraph
import com.example.cst438_project2.oauth.PKCEUtil
import com.example.cst438_project2.oauth.buildGithubAuthUrl
import com.example.cst438_project2.ui.viewmodel.AdminViewModel
import com.example.cst438_project2.ui.viewmodel.ApiViewModel
import com.example.cst438_project2.ui.viewmodel.AppViewModelFactory
import com.example.cst438_project2.ui.viewmodel.RecommendedGamesViewModel
import com.example.cst438_project2.ui.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load any previously saved access token.
        val authStorage = AuthStorage(this)
        val token = authStorage.loadAccessToken()

        enableEdgeToEdge()

        setContent {
            if (token == null) {
                AppNavGraph(
                    // The initial destination depends on whether a token exists.
                    isLoggedIn = false,
                    token = null,

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
                    onLogoutClick = {
                        authStorage.clear()
                    }
                )
            } else {
                // Create Retrofit with the saved GitHub access token.
                val retrofit = RetrofitClient.create(token)

                // Create each repository with its corresponding Retrofit service.
                val userRepository = UserRepository(
                    retrofit.create(UserService::class.java)
                )

                val apiRepository = ApiRepository(
                    retrofit.create(ApiService::class.java)
                )

                val recommendedGamesRepository =
                    RecommendedGamesRepository(
                        retrofit.create(
                            RecommendedGamesService::class.java
                        )
                    )

                val adminRepository = AdminRepository(
                    retrofit.create(AdminService::class.java)
                )

                // Create all ViewModels with their corresponding repositories.
                val factory = AppViewModelFactory(
                    userRepository = userRepository,
                    apiRepository = apiRepository,
                    recommendedGamesRepository =
                        recommendedGamesRepository,
                    adminRepository = adminRepository
                )

                val userViewModel: UserViewModel =
                    viewModel(factory = factory)

                val apiViewModel: ApiViewModel =
                    viewModel(factory = factory)

                val recommendedGamesViewModel:
                        RecommendedGamesViewModel =
                    viewModel(factory = factory)

                val adminViewModel: AdminViewModel =
                    viewModel(factory = factory)

                // Load the authenticated user's data when the app starts.
                LaunchedEffect(token) {
                    userViewModel.loadUser(token)
                    apiViewModel.loadGames(token)
                    recommendedGamesViewModel
                        .loadRecommendations(token)
                }

                AppNavGraph(
                    // A saved token means the user starts at the game list.
                    isLoggedIn = true,

                    // Pass the actual GitHub access token to the graph.
                    token = token,

                    // Pass the ViewModels so screens can use real backend data.
                    userViewModel = userViewModel,
                    apiViewModel = apiViewModel,
                    recommendedGamesViewModel = recommendedGamesViewModel,
                    adminViewModel = adminViewModel,

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
                    onLogoutClick = {
                        authStorage.clear()
                    }
                )
            }
        }
    }
}