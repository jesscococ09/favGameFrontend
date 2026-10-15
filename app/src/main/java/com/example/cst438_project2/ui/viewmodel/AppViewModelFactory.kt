package com.example.cst438_project2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cst438_project2.data.repository.AdminRepository
import com.example.cst438_project2.data.repository.ApiRepository
import com.example.cst438_project2.data.repository.RecommendedGamesRepository
import com.example.cst438_project2.data.repository.UserRepository

class AppViewModelFactory(
    private val userRepository: UserRepository,
    private val apiRepository: ApiRepository,
    private val recommendedGamesRepository: RecommendedGamesRepository,
    private val adminRepository: AdminRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(UserViewModel::class.java) ->
                UserViewModel(userRepository) as T
            modelClass.isAssignableFrom(ApiViewModel::class.java) ->
                ApiViewModel(apiRepository) as T
            modelClass.isAssignableFrom(RecommendedGamesViewModel::class.java) ->
                RecommendedGamesViewModel(recommendedGamesRepository) as T
            modelClass.isAssignableFrom(AdminViewModel::class.java) ->
                AdminViewModel(adminRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}