package com.example.cst438_project2.data.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val token: String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        if (!token.isNullOrBlank()) {
            requestBuilder.header(
                "Authorization",
                "Bearer $token"
            )
        }
        val request = requestBuilder.build()
        println("AUTH INTERCEPTOR: Authorization header added")
        return chain.proceed(request)
    }
}