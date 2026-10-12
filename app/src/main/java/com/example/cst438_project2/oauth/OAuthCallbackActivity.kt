package com.example.cst438_project2.oauth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.cst438_project2.MainActivity
import com.example.cst438_project2.data.AuthStorage
import com.example.cst438_project2.BuildConfig
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class OAuthCallbackActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.data
        Log.d("OAuth", "Callback URI: $uri")

        if (uri != null && uri.scheme == "favgame" && uri.host == "callback") {
            val code = uri.getQueryParameter("code")
            Log.d("OAuth", "Authorization code: $code")

            if (code != null) {
                exchangeCodeForToken(code)
            } else {
                Log.d("OAuth", "No code found in redirect")
                finish()
            }
        } else {
            Log.d("OAuth", "Invalid redirect URI")
            finish()
        }
    }

    private fun exchangeCodeForToken(code: String) {
        val authStorage = AuthStorage(this)
        val verifier = authStorage.loadCodeVerifier()

        Log.d("OAuth", "Loaded PKCE verifier: $verifier")

        if (verifier == null) {
            Log.d("OAuth", "Verifier missing — cannot continue")
            finish()
            return
        }

        val clientId = BuildConfig.GITHUB_CLIENT_ID
        val redirectUri = "favgame://callback"

        val body = FormBody.Builder()
            .add("client_id", clientId)
            .add("client_secret", BuildConfig.GITHUB_CLIENT_SECRET)
            .add("code", code)
            .add("redirect_uri", redirectUri)
            .add("grant_type", "authorization_code")
            .add("code_verifier", verifier)
            .build()

        val request = Request.Builder()
            .url("https://github.com/login/oauth/access_token")
            .post(body)
            .header("Accept", "application/json")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.d("OAuth", "Network failure: ${e.message}")
                runOnUiThread { finish() }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("OAuth", "Raw response: $responseBody")

                if (responseBody == null) {
                    Log.d("OAuth", "Response body null")
                    runOnUiThread { finish() }
                    return
                }

                val json = JSONObject(responseBody)

                if (json.has("error")) {
                    Log.d("OAuth", "OAuth error: ${json.getString("error")}")
                    Log.d("OAuth", "Description: ${json.optString("error_description")}")
                    runOnUiThread { finish() }
                    return
                }

                val token = json.optString("access_token", null)

                if (token == null) {
                    Log.d("OAuth", "Token missing in response")
                    runOnUiThread { finish() }
                    return
                }

                Log.d("OAuth", "Access token received: $token")

                authStorage.saveAccessToken(token)
                Log.d("OAuth", "Token saved successfully")

                runOnUiThread {
                    startActivity(Intent(this@OAuthCallbackActivity, MainActivity::class.java))
                    finish()
                }
            }
        })
    }
}
