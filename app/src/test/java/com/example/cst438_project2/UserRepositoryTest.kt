package com.example.cst438_project2

import com.example.cst438_project2.data.api.UserService
import com.example.cst438_project2.data.repository.UserRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class UserRepositoryTest {

    private lateinit var server: MockWebServer
    private lateinit var service: UserService
    private lateinit var repo: UserRepository

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()

        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserService::class.java)

        repo = UserRepository(service)
    }

    @After
    fun teardown() {
        server.shutdown()
    }
    @Test
    fun `repository adds Bearer prefix`() = runBlocking {
        server.enqueue(MockResponse().setBody("""{"githubId":"1","displayName":"Jessika","iconUrl":""}"""))
        repo.getUser("abc123")
        val request = server.takeRequest()
        assertEquals("Bearer abc123", request.getHeader("Authorization"))
    }
}
