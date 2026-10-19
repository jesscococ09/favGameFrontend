package com.example.cst438_project2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cst438_project2.data.model.Api
import com.example.cst438_project2.data.repository.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {
    private val _games = MutableStateFlow<List<Api>>(emptyList())
    val games: StateFlow<List<Api>> = _games
    private val _selectedGame = MutableStateFlow<Api?>(null)
    val selectedGame: StateFlow<Api?> = _selectedGame
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    fun loadGames(
        token: String
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                val result = repository.listMyGames(token)
                _games.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun searchGames(token: String, query: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                _games.value = if (query.isBlank()) {
                    repository.listMyGames(token)
                } else {
                    repository.searchMyGames(token, query)
                }
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
                _error.value = null
                val result =
                    repository.getMyGame(token, id)
                _selectedGame.value = result
            } catch (e: Exception) {
                _error.value = e.message

            } finally {
                _loading.value = false
            }
        }
    }

    fun addGame(token: String, body: Map<String, Any>) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                repository.addGame(token, body)
                _games.value = repository.listMyGames(token)
            } catch (e: Exception) {
                _error.value = e.message

            } finally {
                _loading.value = false
            }
        }
    }

    fun replaceGame(token: String, id: String, body: Map<String, Any>) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                val updatedGame =
                    repository.replaceGame(token, id, body)
                _selectedGame.value = updatedGame
                _games.value = repository.listMyGames(token)
            } catch (e: Exception) {
                _error.value = e.message

            } finally {
                _loading.value = false
            }
        }
    }

    fun updateGame(token: String, id: String, body: Map<String, Any>) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                val updatedGame =
                    repository.updateGame(token, id, body)
                _selectedGame.value = updatedGame
                _games.value = repository.listMyGames(token)
            } catch (e: Exception) {
                _error.value = e.message

            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteGame(token: String, id: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                repository.deleteGame(token, id)
                _games.value = repository.listMyGames(token)
                if (_selectedGame.value?.id == id) {
                    _selectedGame.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message

            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteGames(
        token: String,
        ids: Set<String>
    ) {
        if (ids.isEmpty()) {
            return
        }
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                ids.forEach { id ->
                    repository.deleteGame(
                        token,
                        id
                    )
                }
                _games.value = repository.listMyGames(token)
                if (_selectedGame.value?.id in ids) {
                    _selectedGame.value = null
                }

            } catch (e: Exception) {
                _error.value = e.message

            } finally {
                _loading.value = false
            }
        }
    }
    fun addComment(
        token: String,
        id: String,
        comment: String
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val updatedGame =
                    repository.addComment(
                        token,
                        id,
                        comment
                    )
                _selectedGame.value = updatedGame
                _games.value = repository.listMyGames(token)
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