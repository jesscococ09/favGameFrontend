package com.example.cst438_project2

import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cst438_project2.oauth.OAuthCallbackActivity
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OAuthCallbackActivityTest {

    @Test
    fun `valid code does not crash`() {
        val intent = Intent().apply {
            setClassName(
                "com.example.cst438_project2",
                "com.example.cst438_project2.oauth.OAuthCallbackActivity"
            )
            data = Uri.parse("favgame://callback?code=abc123")
        }
        val scenario = ActivityScenario.launch<OAuthCallbackActivity>(intent)
        assertTrue(true)
    }
    @Test
    fun `invalid uri does not crash`() {
        val intent = Intent().apply {
            setClassName(
                "com.example.cst438_project2",
                "com.example.cst438_project2.oauth.OAuthCallbackActivity"
            )
            data = Uri.parse("favgame://wrong")
        }
        val scenario = ActivityScenario.launch<OAuthCallbackActivity>(intent)
        assertTrue(true)
    }
}
