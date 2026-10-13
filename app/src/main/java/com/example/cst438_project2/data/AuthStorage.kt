package com.example.cst438_project2.data

import android.content.Context
import android.content.SharedPreferences

class AuthStorage(context: Context) {
    private val prefs: SharedPreferences= context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    fun saveCodeVerifier(verifier: String) {
        prefs.edit().putString("code_verifier", verifier).apply()
    }
    fun loadCodeVerifier(): String? = prefs.getString("code_verifier", null)
    fun saveAccessToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }
    fun loadAccessToken(): String? = prefs.getString("access_token", null)
    fun clear() {
        prefs.edit().clear().apply()
    }
}