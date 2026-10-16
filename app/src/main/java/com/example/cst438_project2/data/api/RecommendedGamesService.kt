package com.example.cst438_project2.data.api

import com.example.cst438_project2.data.model.RecommendedGame
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RecommendedGamesService {
    @GET("recommendations")
    suspend fun listRecommendations(
        @Header("Authorization")
        token: String
    ): List<RecommendedGame>
    @POST("recommendations")
    suspend fun createRecommendation(
        @Header("Authorization")
        token: String,
        @Body
        body: Map<String, @JvmSuppressWildcards Any>
    ): RecommendedGame
    @PATCH("recommendations/{id}")
    suspend fun updateRecommendation(
        @Header("Authorization")
        token: String,
        @Path("id")
        id: String,
        @Body
        body: Map<String, @JvmSuppressWildcards Any>
    ): RecommendedGame
    @PUT("recommendations/{id}")
    suspend fun replaceRecommendation(
        @Header("Authorization")
        token: String,
        @Path("id")
        id: String,
        @Body
        body: Map<String, @JvmSuppressWildcards Any>
    ): RecommendedGame
    @DELETE("recommendations/{id}")
    suspend fun deleteRecommendation(
        @Header("Authorization")
        token: String,
        @Path("id")
        id: String
    ): Map<String, String>
}