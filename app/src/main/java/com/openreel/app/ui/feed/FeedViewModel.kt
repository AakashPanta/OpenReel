package com.openreel.app.ui.feed

import androidx.lifecycle.ViewModel
import com.openreel.app.data.model.VideoPost
import com.openreel.app.data.repository.MockOpenReelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FeedUiState(
    val selectedTab: String = "For You",
    val videos: List<VideoPost> = emptyList()
)

class FeedViewModel : ViewModel() {
    private val repository = MockOpenReelRepository()
    private val _uiState = MutableStateFlow(
        FeedUiState(videos = repository.feed())
    )
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    fun setTab(label: String) {
        _uiState.value = _uiState.value.copy(selectedTab = label)
    }
}
