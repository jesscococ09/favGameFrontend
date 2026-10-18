package com.example.cst438_project2

import com.example.cst438_project2.data.api.AdminService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdminServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var service: AdminService

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()

        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AdminService::class.java)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `updateUser sends correct body`() = runBlocking {
        val mockJson = """
            {
                "id": "1",
                "githubId": "123",
                "displayName": "Random",
                "iconUrl": "",
                "isAdmin": true
            }
        """.trimIndent()

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockJson)
        )

        val result = service.updateUser(
            "Bearer token",
            "1",
            mapOf(
                "isAdmin" to true
            )
        )

        assertEquals("Random", result.displayName)
        assertEquals(true, result.isAdmin)

        val request = server.takeRequest()

        assertEquals("/admin/1", request.path)
        assertEquals(
            "Bearer token",
            request.getHeader("Authorization")
        )
        assertEquals(
            """{"isAdmin":true}""",
            request.body.readUtf8()
        )
    }
}