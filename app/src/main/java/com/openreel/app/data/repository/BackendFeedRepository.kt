package com.openreel.app.data.repository

import com.openreel.app.data.model.Creator
import com.openreel.app.data.model.VideoPost
import com.openreel.app.data.model.VideoStat
import com.openreel.app.data.remote.FeedItemDto
import com.openreel.app.data.remote.OpenReelApiService
import java.util.Locale

class BackendFeedRepository(
    private val api: OpenReelApiService,
    private val fallback: MockOpenReelRepository
) {
    private val fallbackVideos by lazy { fallback.feed() }

    suspend fun feed(tabLabel: String): List<VideoPost> {
        val normalizedTab = if (tabLabel.equals("Following", ignoreCase = true)) {
            "following"
        } else {
            "for_you"
        }

        return runCatching {
            api.getFeed(tab = normalizedTab).items.mapIndexed { index, item ->
                item.toVideoPost(index, normalizedTab)
            }
        }.getOrElse {
            fallbackFeed(tabLabel)
        }
    }

    private fun fallbackFeed(tabLabel: String): List<VideoPost> {
        val all = fallback.feed()
        return if (tabLabel.equals("Following", ignoreCase = true)) {
            all.filter { it.isFollowingCreator }.ifEmpty { all }
        } else {
            all
        }
    }

    private fun FeedItemDto.toVideoPost(index: Int, normalizedTab: String): VideoPost {
        val seed = fallbackVideos[index % fallbackVideos.size]

        val resolvedPlaybackUrl = if (playbackUrl.contains("cdn.openreel.local")) {
            seed.videoUrl
        } else {
            playbackUrl
        }

        return VideoPost(
            id = id,
            creator = Creator(
                id = creator.id,
                displayName = creator.displayName,
                handle = if (creator.handle.startsWith("@")) creator.handle else "@${creator.handle}",
                avatarText = avatarTextFor(creator.displayName),
                verified = creator.verified,
                followers = seed.creator.followers
            ),
            title = title,
            caption = caption,
            videoUrl = resolvedPlaybackUrl,
            tags = tags,
            category = if (normalizedTab == "following") "Following" else "For You",
            durationLabel = formatDuration(durationSec),
            stat = VideoStat(
                likes = prettyCount(stats.likes),
                comments = prettyCount(stats.comments),
                shares = prettyCount(stats.shares),
                saves = prettyCount(stats.saves)
            ),
            palette = seed.palette,
            watchedPercent = seed.watchedPercent,
            isFollowingCreator = normalizedTab == "following" || reasons.any { it.code == "follow_affinity" },
            isLiked = false,
            isSaved = false
        )
    }

    private fun avatarTextFor(displayName: String): String {
        val parts = displayName
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)

        if (parts.isEmpty()) return "OR"

        return parts.joinToString("") { it.first().uppercase() }
    }

    private fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format(Locale.US, "%02d:%02d", mins, secs)
    }

    private fun prettyCount(value: Int): String {
        return when {
            value >= 1_000_000 -> {
                val formatted = String.format(Locale.US, "%.1f", value / 1_000_000.0)
                "${formatted.removeSuffix(".0")}M"
            }
            value >= 1_000 -> {
                val formatted = String.format(Locale.US, "%.1f", value / 1_000.0)
                "${formatted.removeSuffix(".0")}K"
            }
            else -> value.toString()
        }
    }
}
