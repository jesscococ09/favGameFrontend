package com.example.cst438_project2.data.api

import com.example.cst438_project2.data.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface UserService {
    @GET("users")
    suspend fun getUser(
        @Header("Authorization")
        token: String
    ): User

    @PATCH("users/name")
    suspend fun updateDisplayName(
        @Header("Authorization")
        token: String,
        @Body
        body: Map<String, String>
    ): User

    @PATCH("users/icon")
    suspend fun updateIcon(
        @Header("Authorization")
        token: String,
        @Body
        body: Map<String, String>
    ): User

    @DELETE("users")
    suspend fun deleteUser(
        @Header("Authorization")
        token: String
    )
}