package com.example.cst438_project2

import com.example.cst438_project2.data.api.UserService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var service: UserService

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()

        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserService::class.java)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `getUser parses correctly`() = runBlocking {
        val mockJson = """
            {
                "githubId": "123",
                "displayName": "Random",
                "iconUrl": ""
            }
        """.trimIndent()
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockJson)
        )
        val result = service.getUser("Bearer token")
        assertEquals("123", result.githubId)
        assertEquals("Random", result.displayName)
        val request = server.takeRequest()
        assertEquals("/users", request.path)
        assertEquals("Bearer token", request.getHeader("Authorization"))
    }
}
