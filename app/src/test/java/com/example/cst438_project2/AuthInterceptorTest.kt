package com.example.cst438_project2

import com.example.cst438_project2.data.api.AuthInterceptor
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {
    private lateinit var server: MockWebServer

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun teardown() { server.shutdown() }

    @Test
    fun `adds Authorization header when token exists`() {
        val client = OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor("abc123")
            ).build()
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{}")
        )
        val request = Request.Builder().url(server.url("/test")).build()
        client.newCall(request).execute().use { assertEquals(200, it.code) }
        val recorded = server.takeRequest()
        assertEquals("Bearer abc123", recorded.getHeader("Authorization"))
    }

    @Test
    fun `does not add Authorization header when token missing`() {
        val client = OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor("")
            ).build()
        server.enqueue(MockResponse().setResponseCode(200).setBody("{}"))
        val request = Request.Builder().url(server.url("/test")).build()
        client.newCall(request).execute().use { assertEquals(200, it.code) }
        val recorded = server.takeRequest()
        assertNull(recorded.getHeader("Authorization"))
    }

    @Test
    fun `replaces existing Authorization header`() {
        val client = OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor("new-token")
            ).build()
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{}")
        )
        val request = Request.Builder()
            .url(server.url("/test"))
            .addHeader(
                "Authorization",
                "Bearer old-token"
            ).build()
        client.newCall(request).execute().use {
            assertEquals(200, it.code)
        }
        val recorded = server.takeRequest()
        assertEquals("Bearer new-token", recorded.getHeader("Authorization"))
        assertEquals(1, recorded.headers.values("Authorization").size)
    }
}