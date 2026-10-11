package com.example.cst438_project2

import com.example.cst438_project2.data.api.RecommendedGamesService
import com.example.cst438_project2.data.model.RecommendedGame
import com.example.cst438_project2.data.repository.RecommendedGamesRepository
import com.example.cst438_project2.ui.viewmodel.RecommendedGamesViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class RecommendedGamesViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var service: RecommendedGamesService
    private lateinit var repo: RecommendedGamesRepository
    private lateinit var vm: RecommendedGamesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        service = mock<RecommendedGamesService>()
        repo = RecommendedGamesRepository(service)
        vm = RecommendedGamesViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    @Test
    fun `loadRecommendations success updates state`() = runTest {
        whenever(service.listRecommendations("Bearer token"))
            .thenReturn(
                listOf(
                    RecommendedGame(
                        id = "1",
                        gameName = "Halo",
                        thumbnail = "",
                        gameDescription = "FPS",
                        platform = "Xbox",
                        genre = "Action",
                        rating = 5,
                        comment = "Great"
                    )
                )
            )

        vm.loadRecommendations("token")
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, vm.recommendations.value.size)
        assertEquals("Halo", vm.recommendations.value[0].gameName)
    }
    @Test
    fun `loadRecommendations error sets error state`() = runTest {
        whenever(service.listRecommendations("Bearer token"))
            .thenThrow(RuntimeException("boom"))

        vm.loadRecommendations("token")
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(vm.error.value != null)
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

        whenever(service.updateRecommendation("Bearer token", "1", mapOf("rating" to 10)))
            .thenReturn(updated)
        whenever(service.listRecommendations("Bearer token"))
            .thenReturn(listOf(updated))
        vm.updateRecommendation("token", "1", mapOf("rating" to 10))
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(10, vm.selected.value?.rating)
    }
}
