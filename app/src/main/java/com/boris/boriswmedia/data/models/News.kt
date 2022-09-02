package com.boris.boriswmedia.data.models


data class News(
    val source: Source,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String,
)

data class Source(
    val id: Any?,
    val name: String,
)