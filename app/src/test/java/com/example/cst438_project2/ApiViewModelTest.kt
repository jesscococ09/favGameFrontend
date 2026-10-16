package com.example.cst438_project2

import com.example.cst438_project2.data.api.ApiService
import com.example.cst438_project2.data.model.Api
import com.example.cst438_project2.data.repository.ApiRepository
import com.example.cst438_project2.ui.viewmodel.ApiViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ApiViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var service: ApiService
    private lateinit var repository: ApiRepository
    private lateinit var viewModel: ApiViewModel

    private val game = Api(
        id = "game-1",
        githubId = "123",
        gameName = "Halo",
        thumbnail = "",
        gameDescription = "FPS",
        platform = "Xbox",
        genre = "Action",
        rating = 5,
        comment = "Great"
    )

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
    fun `loadGames success updates games state`() = runTest {
        whenever(
            service.listMyGames(
                "Bearer token",
                null,
                null,
                null,
                null
            )
        ).thenReturn(listOf(game))

        viewModel.loadGames("token")
        advanceUntilIdle()

        assertEquals(1, viewModel.games.value.size)
        assertEquals("game-1", viewModel.games.value[0].id)
        assertEquals("Halo", viewModel.games.value[0].gameName)
        assertNull(viewModel.error.value)
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
        ).thenThrow(RuntimeException("boom"))

        viewModel.loadGames("token")
        advanceUntilIdle()

        assertEquals("boom", viewModel.error.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `searchGames with query updates games state`() = runTest {
        whenever(
            service.searchMyGames(
                "Bearer token",
                "Halo"
            )
        ).thenReturn(listOf(game))

        viewModel.searchGames(
            token = "token",
            query = "Halo"
        )

        advanceUntilIdle()

        assertEquals(1, viewModel.games.value.size)
        assertEquals("Halo", viewModel.games.value[0].gameName)
        assertNull(viewModel.error.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `searchGames with blank query loads all games`() = runTest {
        whenever(
            service.listMyGames(
                "Bearer token",
                null,
                null,
                null,
                null
            )
        ).thenReturn(listOf(game))

        viewModel.searchGames(
            token = "token",
            query = ""
        )

        advanceUntilIdle()

        assertEquals(1, viewModel.games.value.size)
        assertEquals("Halo", viewModel.games.value[0].gameName)
        assertNull(viewModel.error.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `loadGame success updates selected game`() = runTest {
        whenever(
            service.getMyGame(
                "Bearer token",
                "game-1"
            )
        ).thenReturn(game)

        viewModel.loadGame(
            token = "token",
            id = "game-1"
        )

        advanceUntilIdle()

        assertNotNull(viewModel.selectedGame.value)
        assertEquals(
            "game-1",
            viewModel.selectedGame.value?.id
        )
        assertEquals(
            "Halo",
            viewModel.selectedGame.value?.gameName
        )
        assertNull(viewModel.error.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `loadGame error sets error state`() = runTest {
        whenever(
            service.getMyGame(
                "Bearer token",
                "game-1"
            )
        ).thenThrow(RuntimeException("game not found"))

        viewModel.loadGame(
            token = "token",
            id = "game-1"
        )

        advanceUntilIdle()

        assertEquals(
            "game not found",
            viewModel.error.value
        )
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `addGame reloads games after successful add`() = runTest {
        val body: Map<String, Any> = mapOf(
            "gameName" to "Halo",
            "thumbnail" to "",
            "gameDescription" to "FPS",
            "platform" to "Xbox",
            "genre" to "Action",
            "rating" to 5,
            "comment" to "Great"
        )

        whenever(
            service.addGame(
                "Bearer token",
                body
            )
        ).thenReturn(game)

        whenever(
            service.listMyGames(
                "Bearer token",
                null,
                null,
                null,
                null
            )
        ).thenReturn(listOf(game))

        viewModel.addGame(
            token = "token",
            body = body
        )

        advanceUntilIdle()

        assertEquals(1, viewModel.games.value.size)
        assertEquals("Halo", viewModel.games.value[0].gameName)
        assertNull(viewModel.error.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `deleteGame removes game and reloads games`() = runTest {
        whenever(
            service.listMyGames(
                "Bearer token",
                null,
                null,
                null,
                null
            )
        ).thenReturn(emptyList())

        viewModel.deleteGame(
            token = "token",
            id = "game-1"
        )

        advanceUntilIdle()

        verify(service).deleteGame(
            "Bearer token",
            "game-1"
        )

        assertEquals(0, viewModel.games.value.size)
        assertNull(viewModel.error.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `deleteGames does nothing when ids are empty`() = runTest {
        viewModel.deleteGames(
            token = "token",
            ids = emptySet()
        )

        advanceUntilIdle()

        assertEquals(0, viewModel.games.value.size)
        assertNull(viewModel.error.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `deleteGames deletes every selected game`() = runTest {
        val ids = setOf(
            "game-1",
            "game-2"
        )

        whenever(
            service.listMyGames(
                "Bearer token",
                null,
                null,
                null,
                null
            )
        ).thenReturn(emptyList())

        viewModel.deleteGames(
            token = "token",
            ids = ids
        )

        advanceUntilIdle()

        verify(service).deleteGame(
            "Bearer token",
            "game-1"
        )

        verify(service).deleteGame(
            "Bearer token",
            "game-2"
        )

        assertEquals(0, viewModel.games.value.size)
        assertNull(viewModel.error.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `clearError removes error state`() = runTest {
        whenever(
            service.getMyGame(
                "Bearer token",
                "missing"
            )
        ).thenThrow(RuntimeException("game not found"))

        viewModel.loadGame(
            token = "token",
            id = "missing"
        )
        advanceUntilIdle()
        assertEquals(
            "game not found",
            viewModel.error.value
        )
        viewModel.clearError()
        assertNull(viewModel.error.value)
    }
}