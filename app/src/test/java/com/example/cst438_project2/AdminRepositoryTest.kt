package com.example.cst438_project2

import com.example.cst438_project2.data.api.AdminService
import com.example.cst438_project2.data.repository.AdminRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdminRepositoryTest {
    private lateinit var server: MockWebServer
    private lateinit var service: AdminService
    private lateinit var repo: AdminRepository

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()

        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AdminService::class.java)

        repo = AdminRepository(service)
    }
    @After
    fun teardown() {
        server.shutdown()
    }
    @Test
    fun `repository adds Bearer prefix`() = runBlocking {
        server.enqueue(
            MockResponse().setBody(
                """{"id":"1","githubId":"123","displayName":"Random","iconUrl":"","isAdmin":false}"""
            )
        )
        repo.getUser("abc123", "1")
        val request = server.takeRequest()
        assertEquals("Bearer abc123", request.getHeader("Authorization"))
    }
}
