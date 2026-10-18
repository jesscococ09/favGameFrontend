package com.example.cst438_project2

import com.example.cst438_project2.data.api.AdminService
import com.example.cst438_project2.data.model.User
import com.example.cst438_project2.data.repository.AdminRepository
import com.example.cst438_project2.ui.viewmodel.AdminViewModel
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
class AdminViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var api: AdminService
    private lateinit var repo: AdminRepository
    private lateinit var vm: AdminViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        api = mock<AdminService>()
        repo = AdminRepository(api)
        vm = AdminViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUsers success updates state`() = runTest {
        whenever(api.listUsers("Bearer token"))
            .thenReturn(
                listOf(
                    User(
                        id = "1",
                        githubId = "123",
                        displayName = "Random",
                        iconUrl = "",
                        isAdmin = false
                    )
                )
            )

        vm.loadUsers("token")
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, vm.users.value.size)
        assertEquals("Random", vm.users.value[0].displayName)
    }

    @Test
    fun `loadUsers error sets error state`() = runTest {
        whenever(api.listUsers("Bearer token"))
            .thenThrow(RuntimeException("boom"))

        vm.loadUsers("token")
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(vm.error.value != null)
    }

    @Test
    fun `toggleAdmin updates selected user`() = runTest {
        val updatedUser = User(
            id = "1",
            githubId = "123",
            displayName = "Random",
            iconUrl = "",
            isAdmin = true
        )

        whenever(
            api.updateUser(
                "Bearer token",
                "1",
                mapOf(
                    "isAdmin" to true
                )
            )
        )
            .thenReturn(updatedUser)

        whenever(api.listUsers("Bearer token"))
            .thenReturn(
                listOf(updatedUser)
            )

        vm.toggleAdmin(
            "token",
            "1",
            true
        )

        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(
            vm.selectedUser.value?.isAdmin == true
        )

        assertEquals(
            1,
            vm.users.value.size
        )

        assertTrue(
            vm.users.value[0].isAdmin
        )
    }
}