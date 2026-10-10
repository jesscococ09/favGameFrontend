package com.example.cst438_project2

import com.example.cst438_project2.data.model.User
import com.example.cst438_project2.data.repository.UserRepository
import com.example.cst438_project2.data.api.UserService
import com.example.cst438_project2.ui.viewmodel.UserViewModel
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
class UserViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var api: UserService
    private lateinit var repo: UserRepository
    private lateinit var vm: UserViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        api = mock<UserService>()
        repo = UserRepository(api)
        vm = UserViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUser success updates state`() = runTest {
        whenever(api.getUser("Bearer token"))
            .thenReturn(
                User(
                    id = "1",
                    githubId = "123",
                    displayName = "Random",
                    iconUrl = "",
                    isAdmin = false
                )
            )

        vm.loadUser("token")
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Random", vm.user.value?.displayName)
    }

    @Test
    fun `loadUser error sets error state`() = runTest {
        whenever(api.getUser("Bearer token"))
            .thenThrow(RuntimeException("boom"))
        vm.loadUser("token")
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.error.value != null)
    }
}
