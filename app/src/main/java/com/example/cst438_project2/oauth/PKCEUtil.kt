package com.example.cst438_project2.oauth

import java.security.SecureRandom
import android.util.Base64
import java.security.MessageDigest

object PKCEUtil {
    fun generateCodeVerifier():String{
        val secureRandom= SecureRandom()
        val code= ByteArray(32)
        secureRandom.nextBytes(code)
        return Base64.encodeToString(code, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
    fun generateCodeChallenge(verifier: String):String{
        val bytes=verifier.toByteArray(Charsets.US_ASCII)
        val md= MessageDigest.getInstance("SHA-256")
        val digest= md.digest(bytes)
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
}