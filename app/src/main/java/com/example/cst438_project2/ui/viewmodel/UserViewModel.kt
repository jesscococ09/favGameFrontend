package com.example.cst438_project2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cst438_project2.data.model.User
import com.example.cst438_project2.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val _deleteSucceeded = MutableStateFlow(false)
    val deleteSucceeded: StateFlow<Boolean> = _deleteSucceeded
    fun loadUser(token: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                val result = repository.getUser(token)
                _user.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
    fun updateDisplayName(
        token: String,
        newName: String
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                val result = repository.updateDisplayName(
                    token,
                    newName
                )
                _user.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
    fun updateIcon(
        token: String,
        newIconUrl: String
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                val result = repository.updateIcon(
                    token,
                    newIconUrl
                )
                _user.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteUser(token: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                _deleteSucceeded.value = false
                repository.deleteUser(token)
                // Only report success after the backend confirms deletion.
                _user.value = null
                _deleteSucceeded.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Unable to delete account."
                _deleteSucceeded.value = false
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun resetDeleteSucceeded() {
        _deleteSucceeded.value = false
    }
}