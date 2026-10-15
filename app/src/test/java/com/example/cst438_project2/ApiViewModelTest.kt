package com.example.cst438_project2

import com.example.cst438_project2.data.api.ApiService
import com.example.cst438_project2.data.model.Api
import com.example.cst438_project2.data.repository.ApiRepository
import com.example.cst438_project2.ui.viewmodel.ApiViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ApiViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var service: ApiService
    private lateinit var repository: ApiRepository
    private lateinit var viewModel: ApiViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        service = mock()
        repository = ApiRepository(service)
        viewModel = ApiViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadGames success updates state`() = runTest {
        val game = Api(
            id = "1",
            githubId = "123",
            gameName = "Halo",
            thumbnail = "",
            gameDescription = "FPS",
            platform = "Xbox",
            genre = "Action",
            rating = 5,
            comment = "Great"
        )
        whenever(
            service.listMyGames(
                "Bearer token",
                null,
                null,
                null,
                null
            )
        ).thenReturn(
            listOf(game)
        )
        viewModel.loadGames("token")
        advanceUntilIdle()
        assertEquals(1, viewModel.games.value.size)
        assertEquals("Halo", viewModel.games.value[0].gameName)
        assertEquals(null, viewModel.error.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `loadGames error sets error state`() = runTest {
        whenever(
            service.listMyGames(
                "Bearer token",
                null,
                null,
                null,
                null
            )
        ).thenThrow(
            RuntimeException("boom")
        )
        viewModel.loadGames("token")
        advanceUntilIdle()
        assertEquals("boom", viewModel.error.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `loadGame success updates selected game`() = runTest {
        val game = Api(
            id = "1",
            githubId = "123",
            gameName = "Halo",
            thumbnail = "",
            gameDescription = "FPS",
            platform = "Xbox",
            genre = "Action",
            rating = 5,
            comment = "Great"
        )

        whenever(
            service.getMyGame(
                "Bearer token",
                "1"
            )
        ).thenReturn(game)

        viewModel.loadGame(token = "token", id = "1")
        advanceUntilIdle()
        assertNotNull(viewModel.selectedGame.value)
        assertEquals("Halo", viewModel.selectedGame.value?.gameName)
        assertEquals(null, viewModel.error.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `loadGame error sets error state`() = runTest {
        whenever(
            service.getMyGame("Bearer token", "1")
        ).thenThrow(
            RuntimeException("game not found")
        )
        viewModel.loadGame(token = "token", id = "1")
        advanceUntilIdle()
        assertEquals("game not found", viewModel.error.value)
        assertFalse(viewModel.loading.value)
    }
}