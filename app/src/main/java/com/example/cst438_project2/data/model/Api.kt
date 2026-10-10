package com.example.cst438_project2.data.model

data class Api(
    val id: String,
    val githubId: String,
    val gameName: String,
    val thumbnail:String,
    val gameDescription: String,
    val platform: String,
    val genre: String,
    val rating: Int,
    val comment: String
)
