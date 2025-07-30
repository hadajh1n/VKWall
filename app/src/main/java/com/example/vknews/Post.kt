package com.example.vknews

data class Post(
    val title: String,
    val text: String,
    val imageUrl: String? = null,
    val isEven: Boolean = false
)
