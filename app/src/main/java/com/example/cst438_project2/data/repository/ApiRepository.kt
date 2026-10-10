package com.example.cst438_project2.data.repository

import com.example.cst438_project2.data.api.ApiService
import com.example.cst438_project2.data.model.Api

class ApiRepository(private  val apiService: ApiService) {
    suspend fun  listMyGames(
        token: String,
        page: Int? = null,
        size: Int? = null,
        sort: String? = null,
        genre: String? = null
    ): List<Api>{
        return apiService.listMyGames("Bearer $token",page,size,sort,genre)
    }
    suspend fun searchMyGames(token: String, query: String): List<Api> {
        return apiService.searchMyGames("Bearer $token", query)
    }
    suspend fun getMyGame(token: String, id: String): Api {
        return apiService.getMyGame("Bearer $token", id)
    }
    suspend fun addGame(token: String, body: Map<String, Any>): Api {
        return apiService.addGame("Bearer $token", body)
    }
    suspend fun replaceGame(token: String, id: String, body: Map<String, Any>): Api {
        return apiService.replaceGame("Bearer $token", id, body)
    }
    suspend fun updateGame(token: String, id: String, body: Map<String, Any>): Api {
        return apiService.updateGame("Bearer $token", id, body)
    }
    suspend fun deleteGame(token: String, id: String) {
        apiService.deleteGame("Bearer $token", id)
    }
    suspend fun addComment(token: String, id: String, comment: String): Api {
        val body = mapOf("comment" to comment)
        return apiService.addComment("Bearer $token", id, body)
    }
}