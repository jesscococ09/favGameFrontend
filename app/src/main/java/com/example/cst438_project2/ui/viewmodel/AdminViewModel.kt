package com.example.cst438_project2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cst438_project2.data.model.User
import com.example.cst438_project2.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: AdminRepository): ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadUsers(token: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                _users.value = repository.listUsers(token)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadUser(token: String, userId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                _selectedUser.value = repository.getUser(token, userId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun toggleAdmin(
        token: String,
        userId: String,
        newValue: Boolean
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                _selectedUser.value = repository.updateUser(
                    token,
                    userId,
                    newValue
                )

                // Reload the user list after changing the user's role.
                _users.value = repository.listUsers(token)
            } catch (e: Exception) {
                _error.value =
                    e.message ?: "Unable to change user role."
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteUser(token: String, userId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                repository.deleteUser(
                    token,
                    userId
                )

                // Reload the user list after deleting the user.
                _users.value = repository.listUsers(token)

                _selectedUser.value = null
            } catch (e: Exception) {
                _error.value =
                    e.message ?: "Unable to delete user."
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}