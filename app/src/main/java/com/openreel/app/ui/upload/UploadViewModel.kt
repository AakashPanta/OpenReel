package com.openreel.app.ui.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openreel.app.core.AppContainer
import com.openreel.app.data.model.UploadSession
import com.openreel.app.data.model.UploadStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UploadUiState(
    val caption: String = "Building OpenReel in public. Explainable feed logic + premium mobile-first UX.",
    val hashtags: List<String> = listOf("opensource", "android", "creatorapp"),
    val selectedVisibility: String = "Public",
    val status: UploadStatus = UploadStatus.Draft,
    val uploadSession: UploadSession? = null,
    val backendMessage: String = "No backend upload session created yet.",
    val errorMessage: String? = null,
    val isSubmitting: Boolean = false
)

class UploadViewModel : ViewModel() {
    private val repository = AppContainer.uploadRepository

    private val _uiState = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    fun updateCaption(value: String) {
        _uiState.update { it.copy(caption = value) }
    }

    fun selectVisibility(value: String) {
        _uiState.update { it.copy(selectedVisibility = value) }
    }

    fun saveDraft() {
        _uiState.update {
            it.copy(
                status = UploadStatus.Draft,
                backendMessage = "Draft saved locally. Backend publish not triggered.",
                errorMessage = null
            )
        }
    }

    fun publish() {
        if (_uiState.value.isSubmitting) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    status = UploadStatus.Uploading(0.08f),
                    backendMessage = "Requesting resumable upload session from the OSS backend...",
                    errorMessage = null
                )
            }

            val result = repository.createUploadSession(
                fileName = "openreel-draft-${System.currentTimeMillis()}.mp4",
                contentType = "video/mp4",
                sizeBytes = 12_500_000L
            )

            result.onSuccess { session ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        status = UploadStatus.Uploading(0.12f),
                        uploadSession = session,
                        backendMessage = "Upload session created. Next step is sending bytes to tusd using the returned upload_url.",
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        status = UploadStatus.Failed,
                        errorMessage = throwable.message ?: "Failed to create upload session",
                        backendMessage = "Backend request failed. Check the API base URL or start services/api."
                    )
                }
            }
        }
    }
}
