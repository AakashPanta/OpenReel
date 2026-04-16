package com.openreel.app.ui.upload

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openreel.app.data.model.UploadSession
import com.openreel.app.data.model.UploadStatus
import com.openreel.app.ui.theme.ReelAccent
import com.openreel.app.ui.theme.ReelBlack
import com.openreel.app.ui.theme.ReelSurfaceAlt
import com.openreel.app.ui.theme.ReelTextMuted

@Composable
fun UploadScreen(viewModel: UploadViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Text(
                text = "Create",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Low-friction publishing for creators with draft, processing, and progress states.",
                style = MaterialTheme.typography.bodyLarge,
                color = ReelTextMuted
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = ReelSurfaceAlt)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    ReelAccent.copy(alpha = 0.7f),
                                    ReelBlack,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Surface(shape = RoundedCornerShape(999.dp), color = ReelBlack.copy(alpha = 0.32f)) {
                        Text(
                            text = "Draft Preview • 00:29",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Surface(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.BottomStart),
                        shape = RoundedCornerShape(20.dp),
                        color = ReelBlack.copy(alpha = 0.38f)
                    ) {
                        Text(
                            text = "Vertical 9:16 • 1080 × 1920",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = ReelSurfaceAlt)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Caption",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = uiState.caption,
                        onValueChange = viewModel::updateCaption,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        shape = RoundedCornerShape(24.dp)
                    )
                    Text(
                        text = uiState.hashtags.joinToString(separator = "   ") { "#$it" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = ReelTextMuted
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = ReelSurfaceAlt)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Visibility",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("Public", "Followers", "Private").forEach { item ->
                            FilterButton(
                                label = item,
                                selected = item == uiState.selectedVisibility,
                                onClick = { viewModel.selectVisibility(item) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = ReelSurfaceAlt)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Upload pipeline",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    when (val status = uiState.status) {
                        is UploadStatus.Uploading -> {
                            Text(
                                text = "Backend session in progress",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            LinearProgressIndicator(
                                progress = { status.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp),
                                color = ReelAccent,
                                trackColor = ReelBlack.copy(alpha = 0.25f)
                            )
                            Text(
                                text = "${(status.progress * 100).toInt()}% complete • resumable upload scaffold ready",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ReelTextMuted
                            )
                        }
                        UploadStatus.Draft -> Text("Saved as draft")
                        UploadStatus.Processing -> Text("Processing variants and thumbnails")
                        UploadStatus.Published -> Text("Published successfully")
                        UploadStatus.Failed -> Text("Upload session failed")
                    }

                    Text(
                        text = uiState.backendMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ReelTextMuted
                    )

                    uiState.errorMessage?.let { error ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = error,
                                modifier = Modifier.padding(14.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    uiState.uploadSession?.let { session ->
                        UploadSessionCard(session = session)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ActionButton(
                            label = "Save Draft",
                            filled = false,
                            enabled = !uiState.isSubmitting,
                            onClick = viewModel::saveDraft
                        )
                        ActionButton(
                            label = if (uiState.isSubmitting) "Connecting..." else "Create Upload Session",
                            filled = true,
                            enabled = !uiState.isSubmitting,
                            onClick = viewModel::publish
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UploadSessionCard(session: UploadSession) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = ReelBlack.copy(alpha = 0.18f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Backend session",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            SessionLine(label = "Upload ID", value = session.uploadId)
            SessionLine(label = "Draft ID", value = session.draftVideoId)
            SessionLine(label = "Status", value = session.status)
            SessionLine(label = "Expires", value = session.expiresAt)
            SessionLine(label = "Upload URL", value = session.uploadUrl)
            SessionLine(label = "Webhook", value = session.processingWebhook)
        }
    }
}

@Composable
private fun SessionLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = ReelTextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FilterButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = if (selected) ReelAccent else ReelBlack.copy(alpha = 0.22f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = if (selected) ReelBlack else MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun ActionButton(
    label: String,
    filled: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(999.dp),
        color = if (filled) ReelAccent else ReelBlack.copy(alpha = 0.22f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            color = if (filled) ReelBlack else MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
