package com.example.cst438_project2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cst438_project2.data.model.Api
import com.example.cst438_project2.data.repository.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ApiViewModel(private val repository: ApiRepository): ViewModel(){
    private val _games = MutableStateFlow<List<Api>>(emptyList())
    val games: StateFlow<List<Api>> = _games
    private val _selectedGame = MutableStateFlow<Api?>(null)
    val selectedGame: StateFlow<Api?> = _selectedGame
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadGames(token: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _games.value = repository.listMyGames(token)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
    fun loadGame(token: String, id: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _selectedGame.value = repository.getMyGame(token, id)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
    fun clearError() {
        _error.value = null
    }
}