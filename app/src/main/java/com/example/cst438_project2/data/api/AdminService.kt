package com.example.cst438_project2.data.api

import com.example.cst438_project2.data.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface AdminService {
    @GET("admin")
    suspend fun listUsers(
        @Header("Authorization")
        token: String
    ): List<User>

    @GET("admin/{userId}")
    suspend fun getUser(
        @Header("Authorization")
        token: String,
        @Path("userId")
        userId: String
    ): User

    @PATCH("admin/{userId}")
    suspend fun updateUser(
        @Header("Authorization")
        token: String,
        @Path("userId")
        userId: String,
        @Body
        body: Map<String, @JvmSuppressWildcards Any>
    ): User

    @DELETE("admin/{userId}")
    suspend fun deleteUser(
        @Header("Authorization")
        token: String,
        @Path("userId")
        userId: String,
    ): Map<String, String>
}