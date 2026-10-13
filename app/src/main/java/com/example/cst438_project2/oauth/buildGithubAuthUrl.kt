package com.example.cst438_project2.oauth

import com.example.cst438_project2.BuildConfig
import okhttp3.Challenge

fun buildGithubAuthUrl(codeChallenge: String): String{
    val clientId= BuildConfig.GITHUB_CLIENT_ID
    val redirectUri= "favgame://callback"
    return "https://github.com/login/oauth/authorize" +
            "?client_id=$clientId" +
            "&redirect_uri=$redirectUri" +
            "&scope=read:user" +
            "&code_challenge=$codeChallenge" +
            "&code_challenge_method=S256"
}