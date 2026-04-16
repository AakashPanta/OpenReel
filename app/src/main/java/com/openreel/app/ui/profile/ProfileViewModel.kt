package com.openreel.app.ui.profile

import androidx.lifecycle.ViewModel
import com.openreel.app.data.model.ProfileHighlight
import com.openreel.app.data.model.VideoPost
import com.openreel.app.data.repository.MockOpenReelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val displayName: String = "Aakash Dev",
    val handle: String = "@aakashbuilds",
    val bio: String = "Building open, premium mobile products with fast UX, transparent logic, and scalable app architecture.",
    val highlights: List<ProfileHighlight> = emptyList(),
    val videos: List<VideoPost> = emptyList()
)

class ProfileViewModel : ViewModel() {
    private val repository = MockOpenReelRepository()
    private val _uiState = MutableStateFlow(
        ProfileUiState(
            highlights = repository.profileHighlights(),
            videos = repository.profileVideos()
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
}
