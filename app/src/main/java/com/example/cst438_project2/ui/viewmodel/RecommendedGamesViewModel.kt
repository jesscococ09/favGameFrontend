package com.example.cst438_project2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cst438_project2.data.model.RecommendedGame
import com.example.cst438_project2.data.repository.RecommendedGamesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecommendedGamesViewModel(
    private val repository: RecommendedGamesRepository
) : ViewModel() {
    private val _recommendations =
        MutableStateFlow<List<RecommendedGame>>(emptyList())
    val recommendations: StateFlow<List<RecommendedGame>> =
        _recommendations
    private val _selected =
        MutableStateFlow<RecommendedGame?>(null)
    val selected: StateFlow<RecommendedGame?> =
        _selected
    private val _loading =
        MutableStateFlow(false)
    val loading: StateFlow<Boolean> =
        _loading
    private val _error =
        MutableStateFlow<String?>(null)
    val error: StateFlow<String?> =
        _error
    fun loadRecommendations(token: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                val result =
                    repository.listRecommendations(token)
                _recommendations.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun createRecommendation(
        token: String,
        body: Map<String, Any>
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                repository.createRecommendation(
                    token,
                    body
                )
                loadRecommendations(token)
            } catch (e: Exception) {
                _error.value = e.message

            } finally {
                _loading.value = false
            }
        }
    }
    fun updateRecommendation(
        token: String,
        id: String,
        body: Map<String, Any>
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                _selected.value =
                    repository.updateRecommendation(
                        token,
                        id,
                        body
                    )
                loadRecommendations(token)
            } catch (e: Exception) {
                _error.value = e.message

            } finally {
                _loading.value = false
            }
        }
    }

    fun replaceRecommendation(
        token: String,
        id: String,
        body: Map<String, Any>
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                _selected.value =
                    repository.replaceRecommendation(
                        token,
                        id,
                        body
                    )

                loadRecommendations(token)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
    fun deleteRecommendation(
        token: String,
        id: String
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                repository.deleteRecommendation(
                    token,
                    id
                )
                loadRecommendations(token)
                _selected.value = null
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