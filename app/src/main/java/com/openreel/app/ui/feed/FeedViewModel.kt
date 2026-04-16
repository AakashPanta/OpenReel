package com.openreel.app.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openreel.app.core.AppContainer
import com.openreel.app.data.model.VideoPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val selectedTab: String = "For You",
    val videos: List<VideoPost> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class FeedViewModel : ViewModel() {
    private val repository = AppContainer.feedRepository

    private val _uiState = MutableStateFlow(
        FeedUiState(
            selectedTab = "For You",
            videos = emptyList(),
            isLoading = true
        )
    )
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadFeed("For You")
    }

    fun setTab(label: String) {
        if (_uiState.value.selectedTab == label && _uiState.value.videos.isNotEmpty()) return
        loadFeed(label)
    }

    private fun loadFeed(label: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedTab = label,
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                repository.feed(label)
            }.onSuccess { videos ->
                _uiState.update {
                    it.copy(
                        videos = videos,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load feed"
                    )
                }
            }
        }
    }
}
