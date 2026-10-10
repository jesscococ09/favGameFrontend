package com.example.cst438_project2.data.repository

import com.example.cst438_project2.data.api.UserService
import com.example.cst438_project2.data.model.User

class UserRepository(private val userService: UserService) {
    suspend fun getUser(token: String): User {
        return userService.getUser("Bearer $token")
    }
    suspend fun updateDisplayName(token: String,newName: String): User{
        val body=mapOf("displayName" to newName)
        return userService.updateDisplayName("Bearer $token",body)
    }
    suspend fun updateIcon(token:String,newIcon: String): User{
        val body=mapOf("iconUrl" to newIcon)
        return userService.updateIcon("Bearer $token",body)
    }
    suspend fun deleteUser(token: String){
        userService.deleteUser("Bearer $token")
    }
}