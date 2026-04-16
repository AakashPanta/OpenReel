package com.openreel.app.data.repository

import androidx.compose.ui.graphics.Color
import com.openreel.app.data.model.Creator
import com.openreel.app.data.model.NotificationItem
import com.openreel.app.data.model.ProfileHighlight
import com.openreel.app.data.model.SuggestedCreator
import com.openreel.app.data.model.TrendingTopic
import com.openreel.app.data.model.VideoPost
import com.openreel.app.data.model.VideoStat

class MockOpenReelRepository : OpenReelRepository {

    private val creators = listOf(
        Creator(
            id = "c1",
            displayName = "Nova Studio",
            handle = "@novastudio",
            avatarText = "NS",
            verified = true,
            followers = "1.3M"
        ),
        Creator(
            id = "c2",
            displayName = "Kai Motion",
            handle = "@kaimotion",
            avatarText = "KM",
            verified = true,
            followers = "842K"
        ),
        Creator(
            id = "c3",
            displayName = "Lina Frames",
            handle = "@linaframes",
            avatarText = "LF",
            followers = "412K"
        ),
        Creator(
            id = "c4",
            displayName = "Open Build Lab",
            handle = "@openbuildlab",
            avatarText = "OB",
            verified = true,
            followers = "224K"
        )
    )

    override fun feed(): List<VideoPost> = listOf(
        VideoPost(
            id = "v1",
            creator = creators[0],
            title = "Designing a transparent short-video feed",
            caption = "How we rank reels without black-box logic. Watch-time, recency, creator-follow affinity, and topic intent all stay explainable.",
            tags = listOf("opensource", "ranking", "productdesign"),
            category = "For You",
            durationLabel = "00:38",
            stat = VideoStat("42.8K", "2.1K", "840", "5.4K"),
            palette = listOf(Color(0xFF0A1020), Color(0xFF1A2B5F), Color(0xFF705CFF)),
            watchedPercent = 0.34f,
            isFollowingCreator = true,
            isLiked = true,
            isSaved = true
        ),
        VideoPost(
            id = "v2",
            creator = creators[1],
            title = "Mobile-first motion systems that feel premium",
            caption = "Swipe transitions should feel tactile, not heavy. The secret is controlled depth, restrained blur, and fast feedback.",
            tags = listOf("motion", "ux", "compose"),
            category = "Following",
            durationLabel = "00:24",
            stat = VideoStat("18.3K", "664", "310", "2.0K"),
            palette = listOf(Color(0xFF09090C), Color(0xFF2B153D), Color(0xFFE261AE)),
            watchedPercent = 0.58f,
            isFollowingCreator = true
        ),
        VideoPost(
            id = "v3",
            creator = creators[2],
            title = "Creator analytics you can actually act on",
            caption = "Retention cliffs, share velocity, replay loops, and follow conversion should be visible from day one.",
            tags = listOf("analytics", "creator", "retention"),
            category = "For You",
            durationLabel = "00:46",
            stat = VideoStat("27.2K", "1.4K", "520", "3.6K"),
            palette = listOf(Color(0xFF080B12), Color(0xFF0E3B41), Color(0xFF17BEBB)),
            watchedPercent = 0.18f
        ),
        VideoPost(
            id = "v4",
            creator = creators[3],
            title = "Backend boundaries for social video apps",
            caption = "Keep media, social graph, notifications, moderation, and feed computation modular from the beginning.",
            tags = listOf("backend", "architecture", "modular"),
            category = "For You",
            durationLabel = "01:02",
            stat = VideoStat("11.7K", "403", "214", "1.3K"),
            palette = listOf(Color(0xFF090A0F), Color(0xFF243344), Color(0xFF9EC5FF)),
            watchedPercent = 0.74f,
            isLiked = true
        )
    )

    override fun trendingTopics(): List<TrendingTopic> = listOf(
        TrendingTopic("t1", "#OpenSource", "14.2K videos"),
        TrendingTopic("t2", "#CreatorTools", "8.3K videos"),
        TrendingTopic("t3", "#ComposeUI", "5.8K videos"),
        TrendingTopic("t4", "#SelfHosted", "3.1K videos"),
        TrendingTopic("t5", "#ProductDesign", "12.9K videos")
    )

    override fun suggestedCreators(): List<SuggestedCreator> = listOf(
        SuggestedCreator(creators[0], "Design systems", "14 mutuals"),
        SuggestedCreator(creators[2], "Creator strategy", "9 mutuals"),
        SuggestedCreator(creators[3], "Infra + backend", "4 mutuals")
    )

    override fun exploreVideos(): List<VideoPost> = feed().shuffled().mapIndexed { index, post ->
        post.copy(id = "e${index}_${post.id}")
    }

    override fun notifications(): List<NotificationItem> = listOf(
        NotificationItem(
            id = "n1",
            title = "Nova Studio followed you",
            body = "Your upload strategy post was added to their saved collection.",
            timeLabel = "Now",
            unread = true
        ),
        NotificationItem(
            id = "n2",
            title = "Your reel is trending in #OpenSource",
            body = "Watch completion is 18% above your weekly average.",
            timeLabel = "12m",
            unread = true
        ),
        NotificationItem(
            id = "n3",
            title = "3 new comments on your video",
            body = "People are asking for the backend folder structure and API contract sample.",
            timeLabel = "1h",
            unread = false
        ),
        NotificationItem(
            id = "n4",
            title = "Upload processed successfully",
            body = "Adaptive variants and preview assets were generated.",
            timeLabel = "Yesterday",
            unread = false
        )
    )

    override fun profileVideos(): List<VideoPost> = feed()

    override fun profileHighlights(): List<ProfileHighlight> = listOf(
        ProfileHighlight("Followers", "128K"),
        ProfileHighlight("Following", "312"),
        ProfileHighlight("Likes", "3.8M"),
        ProfileHighlight("Avg Watch", "72%")
    )
}
