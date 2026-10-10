package com.example.cst438_project2

import com.example.cst438_project2.data.api.ApiService
import com.example.cst438_project2.data.repository.ApiRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiRepositoryTest {

    private lateinit var server: MockWebServer
    private lateinit var service: ApiService
    private lateinit var repo: ApiRepository

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        repo = ApiRepository(service)
    }
    @After
    fun teardown() {
        server.shutdown()
    }
    @Test
    fun `repository adds Bearer prefix`() = runBlocking {
        server.enqueue(MockResponse().setBody("[]"))
        repo.listMyGames("abc123")
        val request = server.takeRequest()
        assertEquals("Bearer abc123", request.getHeader("Authorization"))
    }
}
