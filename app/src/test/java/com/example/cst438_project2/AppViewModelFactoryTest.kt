package com.example.cst438_project2

import androidx.lifecycle.ViewModel
import com.example.cst438_project2.data.api.AdminService
import com.example.cst438_project2.data.api.ApiService
import com.example.cst438_project2.data.api.RecommendedGamesService
import com.example.cst438_project2.data.api.UserService
import com.example.cst438_project2.data.repository.AdminRepository
import com.example.cst438_project2.data.repository.ApiRepository
import com.example.cst438_project2.data.repository.RecommendedGamesRepository
import com.example.cst438_project2.data.repository.UserRepository
import com.example.cst438_project2.ui.viewmodel.AdminViewModel
import com.example.cst438_project2.ui.viewmodel.ApiViewModel
import com.example.cst438_project2.ui.viewmodel.AppViewModelFactory
import com.example.cst438_project2.ui.viewmodel.RecommendedGamesViewModel
import com.example.cst438_project2.ui.viewmodel.UserViewModel
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class AppViewModelFactoryTest {
    private lateinit var userService: UserService
    private lateinit var apiService: ApiService
    private lateinit var recommendedGamesService: RecommendedGamesService
    private lateinit var adminService: AdminService
    private lateinit var userRepository: UserRepository
    private lateinit var apiRepository: ApiRepository
    private lateinit var recommendedGamesRepository: RecommendedGamesRepository
    private lateinit var adminRepository: AdminRepository
    private lateinit var factory: AppViewModelFactory

    @Before
    fun setup() {
        userService = mock()
        apiService = mock()
        recommendedGamesService = mock()
        adminService = mock()

        userRepository = UserRepository(userService)
        apiRepository = ApiRepository(apiService)
        recommendedGamesRepository =
            RecommendedGamesRepository(recommendedGamesService)
        adminRepository = AdminRepository(adminService)

        factory = AppViewModelFactory(
            userRepository,
            apiRepository,
            recommendedGamesRepository,
            adminRepository
        )
    }

    @Test
    fun `creates UserViewModel`() {
        val viewModel = factory.create(UserViewModel::class.java)
        assertTrue(viewModel is UserViewModel)
    }

    @Test
    fun `creates ApiViewModel`() {
        val viewModel = factory.create(ApiViewModel::class.java)
        assertTrue(viewModel is ApiViewModel)
    }

    @Test
    fun `creates RecommendedGamesViewModel`() {
        val viewModel = factory.create(RecommendedGamesViewModel::class.java)
        assertTrue(viewModel is RecommendedGamesViewModel)
    }

    @Test
    fun `creates AdminViewModel`() {
        val viewModel = factory.create(AdminViewModel::class.java)
        assertTrue(viewModel is AdminViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `unknown ViewModel throws exception`() {
        factory.create(UnknownViewModel::class.java)
    }
    private class UnknownViewModel : ViewModel()
}