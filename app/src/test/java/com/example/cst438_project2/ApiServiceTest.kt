package com.example.cst438_project2

import com.example.cst438_project2.data.api.ApiService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var service: ApiService

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()

        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `listMyGames parses correctly`() = runBlocking {
        val mockJson = """
            [
                {
                    "id": "1",
                    "githubId": "123",
                    "gameName": "Halo",
                    "thumbnail": "",
                    "gameDescription": "FPS",
                    "platform": "Xbox",
                    "genre": "Action",
                    "rating": 5,
                    "comment": "Great"
                }
            ]
        """.trimIndent()
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockJson)
        )
        val result = service.listMyGames("Bearer token")
        assertEquals(1, result.size)
        assertEquals("Halo", result[0].gameName)
        assertEquals(5, result[0].rating)
        val request = server.takeRequest()
        assertEquals("/users/games", request.path)
        assertEquals("Bearer token", request.getHeader("Authorization"))
    }
}
