package com.example.cst438_project2

import com.example.cst438_project2.oauth.buildGithubAuthUrl
import junit.framework.TestCase.assertTrue
import org.junit.Test

class BuildGithubAuthUrlTest {

    @Test
    fun `builds correct GitHub OAuth URL`() {
        val verifier = "testVerifier"
        val url = buildGithubAuthUrl(verifier)
        assertTrue(url.startsWith("https://github.com/login/oauth/authorize"))
        assertTrue(url.contains("client_id="))
        assertTrue(url.contains("redirect_uri=favgame://callback"))
        assertTrue(url.contains("code_challenge="))
        assertTrue(url.contains("code_challenge_method=S256"))
    }
}
