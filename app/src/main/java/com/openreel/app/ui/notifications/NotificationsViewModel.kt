package com.openreel.app.ui.notifications

import androidx.lifecycle.ViewModel
import com.openreel.app.data.model.NotificationItem
import com.openreel.app.data.repository.MockOpenReelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NotificationsUiState(
    val items: List<NotificationItem> = emptyList()
)

class NotificationsViewModel : ViewModel() {
    private val repository = MockOpenReelRepository()
    private val _uiState = MutableStateFlow(NotificationsUiState(items = repository.notifications()))
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()
}
