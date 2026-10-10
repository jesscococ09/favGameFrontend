package com.example.cst438_project2.data.model

data class User(
    val id: String?,
    val githubId: String,
    val displayName: String,
    val iconUrl: String?,
    val isAdmin: Boolean
)

