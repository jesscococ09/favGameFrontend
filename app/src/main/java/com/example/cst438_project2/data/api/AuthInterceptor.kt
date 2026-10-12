package com.example.cst438_project2.data.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val token: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        if (!token.isNullOrBlank()) {
            builder.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(builder.build())
    }
}
