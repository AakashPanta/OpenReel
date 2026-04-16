package com.openreel.app.domain.model

data class Video(
    val id: String,
    val videoUrl: String,
    val thumbnailUrl: String = "",
    val caption: String,
    val creator: String,
    val likes: Int
)
