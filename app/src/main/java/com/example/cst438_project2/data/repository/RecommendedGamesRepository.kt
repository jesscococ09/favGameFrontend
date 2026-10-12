package com.example.cst438_project2.data.repository

import com.example.cst438_project2.data.api.RecommendedGamesService
import com.example.cst438_project2.data.model.RecommendedGame

class RecommendedGamesRepository(private val service: RecommendedGamesService) {
    suspend fun listRecommendations(token: String): List<RecommendedGame>{
        return service.listRecommendations("Bearer $token")
    }
    suspend fun createRecommendation(token: String,body: Map<String,Any>): RecommendedGame{
        return service.createRecommendation("Bearer $token",body)
    }
    suspend fun updateRecommendation(token: String,id: String,body: Map<String, Any>): RecommendedGame{
        return service.updateRecommendation("Bearer $token",id,body)
    }
    suspend fun replaceRecommendation(token: String,id: String,body: Map<String, Any>): RecommendedGame{
        return service.replaceRecommendation("Bearer $token",id,body)
    }
    suspend fun deleteRecommendation(token: String,id: String): Map<String,String>{
        return service.deleteRecommendation("Bearer $token",id)
    }
}