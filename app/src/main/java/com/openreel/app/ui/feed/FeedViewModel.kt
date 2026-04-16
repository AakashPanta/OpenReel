package com.openreel.app.ui.feed

import androidx.lifecycle.ViewModel
import com.openreel.app.data.model.VideoPost
import com.openreel.app.data.repository.MockOpenReelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FeedUiState(
    val selectedTab: String = "For You",
    val videos: List<VideoPost> = emptyList()
)

class FeedViewModel : ViewModel() {
    private val repository = MockOpenReelRepository()
    private val allVideos = repository.feed()

    private val _uiState = MutableStateFlow(
        FeedUiState(
            selectedTab = "For You",
            videos = filterVideos("For You")
        )
    )
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    fun setTab(label: String) {
        _uiState.update {
            it.copy(
                selectedTab = label,
                videos = filterVideos(label)
            )
        }
    }

    private fun filterVideos(tab: String): List<VideoPost> {
        val filtered = when (tab) {
            "Following" -> allVideos.filter { it.isFollowingCreator }
            else -> allVideos
        }
        return if (filtered.isEmpty()) allVideos else filtered
    }
}
