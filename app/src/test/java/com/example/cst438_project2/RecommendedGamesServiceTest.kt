package com.example.cst438_project2

import com.example.cst438_project2.data.model.RecommendedGame
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.GET

data class RecUpdateBody(
    val gameName: String,
    val rating: Int
)

interface TestRecommendedGamesService {
    @GET("recommendations")
    suspend fun listRecommendations(
        @Header("Authorization") token: String
    ): List<RecommendedGame>
    @PATCH("recommendations/{id}")
    suspend fun updateRecommendation(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body body: RecUpdateBody
    ): RecommendedGame
}

class RecommendedGamesServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var service: TestRecommendedGamesService

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TestRecommendedGamesService::class.java)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `listRecommendations parses correctly`() = runBlocking {
        val mockJson = """
            [
                {
                    "id": "1",
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
        server.enqueue(MockResponse().setResponseCode(200).setBody(mockJson))
        val result = service.listRecommendations("Bearer token")
        assertEquals(1, result.size)
        assertEquals("Halo", result[0].gameName)
        val request = server.takeRequest()
        assertEquals("/recommendations", request.path)
        assertEquals("Bearer token", request.getHeader("Authorization"))
    }
    @Test
    fun `updateRecommendation sends correct body`() = runBlocking {
        val mockJson = """
            {
                "id": "1",
                "gameName": "Halo Updated",
                "thumbnail": "",
                "gameDescription": "FPS",
                "platform": "Xbox",
                "genre": "Action",
                "rating": 10,
                "comment": "Updated"
            }
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(mockJson))
        val result = service.updateRecommendation(
            "Bearer token",
            "1",
            RecUpdateBody("Halo Updated", 10)
        )
        assertEquals("Halo Updated", result.gameName)
        assertEquals(10, result.rating)
        val request = server.takeRequest()
        assertEquals("/recommendations/1", request.path)
        assertEquals("""{"gameName":"Halo Updated","rating":10}""", request.body.readUtf8())
    }
}
