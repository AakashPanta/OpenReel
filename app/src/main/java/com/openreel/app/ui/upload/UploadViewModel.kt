package com.openreel.app.ui.upload

import androidx.lifecycle.ViewModel
import com.openreel.app.data.model.UploadStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UploadUiState(
    val caption: String = "Building OpenReel in public. Explainable feed logic + premium mobile-first UX.",
    val hashtags: List<String> = listOf("opensource", "android", "creatorapp"),
    val selectedVisibility: String = "Public",
    val status: UploadStatus = UploadStatus.Uploading(0.64f)
)

class UploadViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    fun updateCaption(value: String) {
        _uiState.value = _uiState.value.copy(caption = value)
    }

    fun selectVisibility(value: String) {
        _uiState.value = _uiState.value.copy(selectedVisibility = value)
    }
}
