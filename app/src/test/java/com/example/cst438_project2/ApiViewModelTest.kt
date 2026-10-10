package com.example.cst438_project2

import com.example.cst438_project2.data.model.Api
import com.example.cst438_project2.data.repository.ApiRepository
import com.example.cst438_project2.data.api.ApiService
import com.example.cst438_project2.ui.viewmodel.ApiViewModel
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
class ApiViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var service: ApiService
    private lateinit var repo: ApiRepository
    private lateinit var vm: ApiViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        service = mock<ApiService>()
        repo = ApiRepository(service)

        vm = ApiViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadGames success updates state`() = runTest {
        whenever(service.listMyGames("Bearer token", null, null, null, null))
            .thenReturn(
                listOf(
                    Api(
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
                )
            )
        vm.loadGames("token")
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, vm.games.value.size)
        assertEquals("Halo", vm.games.value[0].gameName)
    }

    @Test
    fun `loadGames error sets error state`() = runTest {
        whenever(service.listMyGames("Bearer token", null, null, null, null))
            .thenThrow(RuntimeException("boom"))
        vm.loadGames("token")
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.error.value != null)
    }
}
