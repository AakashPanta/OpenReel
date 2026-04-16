package com.openreel.app.data.repository

import com.openreel.app.data.model.NotificationItem
import com.openreel.app.data.model.ProfileHighlight
import com.openreel.app.data.model.SuggestedCreator
import com.openreel.app.data.model.TrendingTopic
import com.openreel.app.data.model.VideoPost

interface OpenReelRepository {
    fun feed(): List<VideoPost>
    fun trendingTopics(): List<TrendingTopic>
    fun suggestedCreators(): List<SuggestedCreator>
    fun exploreVideos(): List<VideoPost>
    fun notifications(): List<NotificationItem>
    fun profileVideos(): List<VideoPost>
    fun profileHighlights(): List<ProfileHighlight>
}
