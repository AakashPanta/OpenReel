package com.openreel.app.ui.explore

import androidx.lifecycle.ViewModel
import com.openreel.app.data.model.SuggestedCreator
import com.openreel.app.data.model.TrendingTopic
import com.openreel.app.data.model.VideoPost
import com.openreel.app.data.repository.MockOpenReelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ExploreUiState(
    val query: String = "",
    val trendingTopics: List<TrendingTopic> = emptyList(),
    val suggestedCreators: List<SuggestedCreator> = emptyList(),
    val featuredVideos: List<VideoPost> = emptyList()
)

class ExploreViewModel : ViewModel() {
    private val repository = MockOpenReelRepository()
    private val _uiState = MutableStateFlow(
        ExploreUiState(
            trendingTopics = repository.trendingTopics(),
            suggestedCreators = repository.suggestedCreators(),
            featuredVideos = repository.exploreVideos()
        )
    )
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }
}
