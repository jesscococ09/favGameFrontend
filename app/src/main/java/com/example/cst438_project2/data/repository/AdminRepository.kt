package com.example.cst438_project2.data.repository

import com.example.cst438_project2.data.api.AdminService
import com.example.cst438_project2.data.model.User

class AdminRepository(private val adminService: AdminService) {
    suspend fun listUsers(token: String): List<User>{
        return adminService.listUsers("Bearer $token")
    }
    suspend fun getUser(token: String,userId:String): User{
        return adminService.getUser("Bearer $token",userId)
    }
    suspend fun updateUser(token: String,userId: String,isAdmin: Boolean): User{
        val body=mapOf("isAdmin" to isAdmin)
        return adminService.updateUser("Bearer $token",userId,body)
    }
    suspend fun deleteUser(token: String,userId: String): Map<String, String>{
        return adminService.deleteUser("Bearer $token",userId)
    }
}