package com.openreel.app.data.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class Creator(
    val id: String,
    val displayName: String,
    val handle: String,
    val avatarText: String,
    val verified: Boolean = false,
    val followers: String = "0"
)

@Immutable
data class VideoStat(
    val likes: String,
    val comments: String,
    val shares: String,
    val saves: String
)

@Immutable
data class VideoPost(
    val id: String,
    val creator: Creator,
    val title: String,
    val caption: String,
    val videoUrl: String,
    val tags: List<String>,
    val category: String,
    val durationLabel: String,
    val stat: VideoStat,
    val palette: List<Color>,
    val watchedPercent: Float = 0f,
    val isFollowingCreator: Boolean = false,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)

@Immutable
data class TrendingTopic(
    val id: String,
    val label: String,
    val posts: String
)

@Immutable
data class SuggestedCreator(
    val creator: Creator,
    val niche: String,
    val mutuals: String
)

@Immutable
data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val timeLabel: String,
    val unread: Boolean = true
)

@Immutable
data class ProfileHighlight(
    val label: String,
    val value: String
)

@Immutable
sealed interface UploadStatus {
    data object Draft : UploadStatus
    data class Uploading(val progress: Float) : UploadStatus
    data object Processing : UploadStatus
    data object Published : UploadStatus
    data object Failed : UploadStatus
}
