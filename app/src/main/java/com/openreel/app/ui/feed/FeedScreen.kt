package com.openreel.app.ui.feed

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.openreel.app.data.model.VideoPost
import com.openreel.app.ui.theme.ReelAccent
import com.openreel.app.ui.theme.ReelBlack
import com.openreel.app.ui.theme.ReelSurfaceAlt
import com.openreel.app.ui.theme.ReelTextMuted
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import androidx.compose.runtime.snapshotFlow

@Composable
fun FeedScreen(viewModel: FeedViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val playerController = remember(context) { FeedPlayerController(context) }
    val pagerState = rememberPagerState(pageCount = { uiState.videos.size })

    DisposableEffect(lifecycleOwner, playerController) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START,
                Lifecycle.Event.ON_RESUME -> playerController.resume()

                Lifecycle.Event.ON_PAUSE,
                Lifecycle.Event.ON_STOP -> playerController.pause()

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            playerController.release()
        }
    }

    LaunchedEffect(uiState.selectedTab, uiState.videos.size) {
        if (uiState.videos.isNotEmpty() && pagerState.currentPage != 0) {
            pagerState.scrollToPage(0)
        }
    }

    LaunchedEffect(uiState.videos, pagerState) {
        snapshotFlow { pagerState.settledPage }
            .filter { uiState.videos.isNotEmpty() }
            .distinctUntilChanged()
            .collect { page ->
                val currentVideo = uiState.videos.getOrNull(page) ?: return@collect
                val nextVideo = uiState.videos.getOrNull(page + 1)

                playerController.play(currentVideo)
                playerController.preload(nextVideo)
            }
    }

    LaunchedEffect(playerController) {
        while (true) {
            playerController.updateProgress()
            delay(200)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.videos.isEmpty()) {
            EmptyFeedState(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ReelBlack)
            )
        } else {
            VerticalPager(
                state = pagerState,
                beyondViewportPageCount = 1,
                key = { page -> uiState.videos[page].id },
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val video = uiState.videos[page]
                val isActive = playerController.activeVideoId == video.id

                ReelPage(
                    video = video,
                    isActive = isActive,
                    player = if (isActive) playerController.player else null,
                    playbackProgress = if (isActive) playerController.progress else video.watchedPercent,
                    isMuted = playerController.isMuted,
                    isPlaying = playerController.isPlaying,
                    isBuffering = isActive && playerController.isBuffering,
                    onToggleMute = playerController::toggleMute,
                    onTogglePlayPause = playerController::togglePlayPause
                )
            }
        }

        FeedHeader(
            selectedTab = uiState.selectedTab,
            onTabSelected = viewModel::setTab,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun FeedHeader(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        listOf("Following", "For You").forEach { label ->
            val selected = selectedTab == label
            Surface(
                onClick = { onTabSelected(label) },
                shape = RoundedCornerShape(999.dp),
                color = if (selected) {
                    ReelAccent.copy(alpha = 0.18f)
                } else {
                    ReelBlack.copy(alpha = 0.34f)
                },
                modifier = Modifier.padding(horizontal = 6.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun ReelPage(
    video: VideoPost,
    isActive: Boolean,
    player: ExoPlayer?,
    playbackProgress: Float,
    isMuted: Boolean,
    isPlaying: Boolean,
    isBuffering: Boolean,
    onToggleMute: () -> Unit,
    onTogglePlayPause: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(video.palette))
    ) {
        if (player != null) {
            ReelVideoPlayer(
                player = player,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            PlaceholderPoster(
                video = video,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            ReelBlack.copy(alpha = 0.12f),
                            Color.Transparent,
                            ReelBlack.copy(alpha = 0.70f)
                        )
                    )
                )
        )

        LinearProgressIndicator(
            progress = { playbackProgress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .statusBarsPadding()
                .height(4.dp)
                .clip(CircleShape),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.18f)
        )

        ReelTopMeta(
            video = video,
            isMuted = isMuted,
            onToggleMute = onToggleMute,
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .statusBarsPadding()
                .padding(top = 30.dp)
        )

        ActionRail(
            video = video,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 14.dp, bottom = 26.dp)
        )

        ReelBottomInfo(
            video = video,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 96.dp, bottom = 24.dp)
        )

        if (isBuffering) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(42.dp)
                    .align(Alignment.Center),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.18f)
            )
        } else if (isActive && !isPlaying) {
            Surface(
                onClick = onTogglePlayPause,
                shape = CircleShape,
                color = ReelBlack.copy(alpha = 0.45f),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(18.dp)
                        .size(34.dp)
                )
            }
        }
    }
}

@Composable
private fun ReelVideoPlayer(
    player: Player,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PlayerView(context).apply {
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                this.player = player
            }
        },
        update = { view ->
            view.player = player
        }
    )
}

@Composable
private fun PlaceholderPoster(
    video: VideoPost,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Brush.verticalGradient(video.palette)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = ReelBlack.copy(alpha = 0.32f)
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(18.dp)
                    .size(38.dp)
            )
        }
    }
}

@Composable
private fun ReelTopMeta(
    video: VideoPost,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = ReelBlack.copy(alpha = 0.34f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "${video.category} • ${video.durationLabel}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            onClick = onToggleMute,
            shape = CircleShape,
            color = ReelBlack.copy(alpha = 0.34f)
        ) {
            Icon(
                imageVector = if (isMuted) Icons.Rounded.VolumeOff else Icons.Rounded.VolumeUp,
                contentDescription = if (isMuted) "Unmute" else "Mute",
                tint = Color.White,
                modifier = Modifier
                    .padding(12.dp)
                    .size(22.dp)
            )
        }
    }
}

@Composable
private fun ActionRail(
    video: VideoPost,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        CreatorAvatar(video = video)

        ActionMetric(
            icon = Icons.Rounded.Favorite,
            label = video.stat.likes,
            active = video.isLiked
        )
        ActionMetric(
            icon = Icons.Rounded.ChatBubbleOutline,
            label = video.stat.comments
        )
        ActionMetric(
            icon = Icons.Rounded.Share,
            label = video.stat.shares
        )
        ActionMetric(
            icon = Icons.Rounded.BookmarkBorder,
            label = video.stat.saves,
            active = video.isSaved
        )
    }
}

@Composable
private fun CreatorAvatar(video: VideoPost) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(ReelBlack.copy(alpha = 0.42f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = video.creator.avatarText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(999.dp),
            color = Color.White
        ) {
            Text(
                text = if (video.isFollowingCreator) "Following" else "Follow",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = ReelBlack,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun ActionMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = if (active) ReelAccent.copy(alpha = 0.24f) else ReelBlack.copy(alpha = 0.34f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (active) ReelAccent else Color.White,
                modifier = Modifier
                    .padding(14.dp)
                    .size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ReelBottomInfo(
    video: VideoPost,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = video.creator.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (video.creator.verified) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Rounded.Verified,
                    contentDescription = null,
                    tint = ReelAccent,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = video.creator.handle,
                style = MaterialTheme.typography.bodyMedium,
                color = ReelTextMuted
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = video.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = video.caption,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.92f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            video.tags.forEach { tag ->
                AssistChip(
                    onClick = {},
                    label = { Text("#$tag") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = ReelSurfaceAlt.copy(alpha = 0.84f),
                        labelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = ReelBlack.copy(alpha = 0.34f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text(
                    text = "Why you are seeing this",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Matched on ${video.tags.first()} + strong completion patterns + creator affinity from similar posts.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.82f)
                )
            }
        }
    }
}

@Composable
private fun EmptyFeedState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No reels available",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
    }
}
