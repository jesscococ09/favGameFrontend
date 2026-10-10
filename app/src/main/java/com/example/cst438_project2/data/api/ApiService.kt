package com.example.cst438_project2.data.api

import com.example.cst438_project2.data.model.Api
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("users/games")
    suspend fun listMyGames(
        @Header("Authorization")
        token: String,
        @Query("page")
        page:Int?=null,
        @Query("size")
        size: Int? = null,
        @Query("sort")
        sort: String? = null,
        @Query("genre")
        genre: String? = null
    ): List<Api>
    @GET("users/games/search")
    suspend fun searchMyGames(
        @Header("Authorization")
        token: String,
        @Query("query")
        query: String
    ): List<Api>
    @GET("users/games/{id}")
    suspend fun getMyGame(
        @Header("Authorization")
        token: String,
        @Path("id")
        id: String
    ): Api

    @POST("users/games")
    suspend fun addGame(
        @Header("Authorization")
        token: String,
        @Body
        body: Map<String, Any>
    ): Api
    @PUT("users/games/{id}")
    suspend fun replaceGame(
        @Header("Authorization")
        token: String,
        @Path("id")
        id: String,
        @Body
        body: Map<String, Any>
    ): Api
    @PATCH("users/games/{id}")
    suspend fun updateGame(
        @Header("Authorization")
        token: String,
        @Path("id")
        id: String,
        @Body
        body: Map<String, Any>
    ): Api
    @DELETE("users/games/{id}")
    suspend fun deleteGame(
        @Header("Authorization")
        token: String,
        @Path("id")
        id: String
    ): Api
    @POST("users/games/{id}/comments")
    suspend fun addComment(
        @Header("Authorization")
        token: String,
        @Path("id")
        id: String,
        @Body
        body: Map<String, Any>
    ): Api

}