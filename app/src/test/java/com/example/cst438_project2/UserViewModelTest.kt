package com.example.cst438_project2

import com.example.cst438_project2.data.api.UserService
import com.example.cst438_project2.data.model.User
import com.example.cst438_project2.data.repository.UserRepository
import com.example.cst438_project2.ui.viewmodel.UserViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
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
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var service: UserService
    private lateinit var repository: UserRepository
    private lateinit var viewModel: UserViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        service = mock()
        repository = UserRepository(service)
        viewModel = UserViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUser success updates state`() = runTest {
        val user = User(
            id = "1",
            githubId = "123",
            displayName = "Random",
            iconUrl = "",
            isAdmin = false
        )
        whenever(
            service.getUser(
                "Bearer token"
            )
        ).thenReturn(user)
        viewModel.loadUser("token")
        advanceUntilIdle()
        assertEquals(
            "Random",
            viewModel.user.value?.displayName
        )
        assertEquals(
            "123",
            viewModel.user.value?.githubId
        )
        assertNull(
            viewModel.error.value
        )
        assertFalse(
            viewModel.loading.value
        )
    }

    @Test
    fun `loadUser error sets error state`() = runTest {
        whenever(
            service.getUser(
                "Bearer token"
            )
        ).thenThrow(
            RuntimeException("boom")
        )
        viewModel.loadUser("token")
        advanceUntilIdle()
        assertEquals(
            "boom",
            viewModel.error.value
        )
        assertNull(
            viewModel.user.value
        )
        assertFalse(
            viewModel.loading.value
        )
    }
}