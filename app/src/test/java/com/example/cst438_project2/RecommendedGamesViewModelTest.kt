package com.example.cst438_project2

import com.example.cst438_project2.data.api.RecommendedGamesService
import com.example.cst438_project2.data.model.RecommendedGame
import com.example.cst438_project2.data.repository.RecommendedGamesRepository
import com.example.cst438_project2.ui.viewmodel.RecommendedGamesViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
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
class RecommendedGamesViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var service: RecommendedGamesService
    private lateinit var repository: RecommendedGamesRepository
    private lateinit var viewModel: RecommendedGamesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        service = mock()
        repository = RecommendedGamesRepository(service)
        viewModel = RecommendedGamesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadRecommendations success updates state`() = runTest {
        val recommendation = RecommendedGame(
            id = "1",
            gameName = "Halo",
            thumbnail = "",
            gameDescription = "FPS",
            platform = "Xbox",
            genre = "Action",
            rating = 5,
            comment = "Great"
        )
        whenever(
            service.listRecommendations(
                "Bearer token"
            )
        ).thenReturn(listOf(recommendation))
        viewModel.loadRecommendations("token")
        advanceUntilIdle()
        assertEquals(1, viewModel.recommendations.value.size)
        assertEquals("Halo", viewModel.recommendations.value[0].gameName)
        assertEquals(null, viewModel.error.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `loadRecommendations accepts empty list`() = runTest {
        whenever(
            service.listRecommendations(
                "Bearer token"
            )
        ).thenReturn(
            emptyList()
        )
        viewModel.loadRecommendations("token")
        advanceUntilIdle()
        assertEquals(
            emptyList<RecommendedGame>(),
            viewModel.recommendations.value
        )
        assertEquals(null, viewModel.error.value)
        assertFalse(viewModel.loading.value)
    }

    @Test
    fun `loadRecommendations error sets error state`() = runTest {
        whenever(
            service.listRecommendations(
                "Bearer token"
            )
        ).thenThrow(
            RuntimeException("boom")
        )
        viewModel.loadRecommendations("token")
        advanceUntilIdle()
        assertEquals(
            "boom",
            viewModel.error.value
        )
        assertFalse(
            viewModel.loading.value
        )
    }

    @Test
    fun `updateRecommendation updates selected`() = runTest {
        val updated = RecommendedGame(
            id = "1",
            gameName = "Halo",
            thumbnail = "",
            gameDescription = "FPS",
            platform = "Xbox",
            genre = "Action",
            rating = 10,
            comment = "Updated"
        )
        val body: Map<String, Any> = mapOf("rating" to 10)
        whenever(
            service.updateRecommendation(
                "Bearer token",
                "1",
                body
            )
        ).thenReturn(updated)
        whenever(
            service.listRecommendations(
                "Bearer token"
            )
        ).thenReturn(listOf(updated))
        viewModel.updateRecommendation(
            token = "token",
            id = "1",
            body = body
        )
        advanceUntilIdle()
        assertEquals(
            10,
            viewModel.selected.value?.rating
        )
        assertEquals(
            "Updated",
            viewModel.selected.value?.comment
        )
        assertEquals(
            null,
            viewModel.error.value
        )
        assertFalse(
            viewModel.loading.value
        )
    }
}