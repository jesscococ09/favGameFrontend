package com.example.cst438_project2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cst438_project2.data.AuthStorage
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthStorageTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    @Test
    fun `save and load access token`() {
        val storage = AuthStorage(context)
        storage.saveAccessToken("xyz789")
        val loaded = storage.loadAccessToken()
        assertEquals("xyz789", loaded)
    }
}
