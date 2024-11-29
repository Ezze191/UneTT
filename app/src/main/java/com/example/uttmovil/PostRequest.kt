package com.example.uttmovil

data class PostRequest(
    val username: String,
    val post: String,
    val mediaURL: String?,
    val date: String,
    val postId : String
)
