package com.openreel.app.ui.feed

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import com.openreel.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

@Composable
fun FeedScreen(viewModel: FeedViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember { FeedPlayerController(context) }
    val pager = rememberPagerState(pageCount = { uiState.videos.size })

    DisposableEffect(Unit) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) controller.resume()
            if (e == Lifecycle.Event.ON_PAUSE) controller.pause()
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(obs)
            controller.release()
        }
    }

    LaunchedEffect(pager.settledPage) {
        val v = uiState.videos.getOrNull(pager.settledPage) ?: return@LaunchedEffect
        controller.play(v)
        controller.preload(uiState.videos.getOrNull(pager.settledPage + 1))
    }

    Box(Modifier.fillMaxSize()) {

        VerticalPager(
            state = pager,
            modifier = Modifier.fillMaxSize()
        ) { page ->

            val video = uiState.videos[page]
            val active = controller.activeVideoId == video.id

            Box(Modifier.fillMaxSize()) {

                if (active) {
                    AndroidView(
                        factory = {
                            PlayerView(it).apply {
                                useController = false
                                player = controller.player
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        Modifier.fillMaxSize().background(
                            Brush.verticalGradient(video.palette)
                        )
                    )
                }

                // DARK OVERLAY
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(0.6f)
                            )
                        )
                    )
                )

                // HEADER FIXED (NO OVERLAP)
                Row(
                    Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Following", "For You").forEach {
                        FilterChip(
                            selected = uiState.selectedTab == it,
                            onClick = { viewModel.setTab(it) },
                            label = { Text(it) }
                        )
                    }
                }

                // RIGHT ACTIONS (SPACING FIX)
                Column(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Icon(Icons.Rounded.Favorite, null)
                    Text(video.stat.likes)

                    Icon(Icons.Rounded.ChatBubbleOutline, null)
                    Text(video.stat.comments)

                    Icon(Icons.Rounded.Share, null)
                    Text(video.stat.shares)
                }

                // BOTTOM CONTENT FIXED WIDTH
                Column(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, end = 90.dp, bottom = 70.dp)
                ) {

                    Text(video.creator.displayName, color = Color.White)

                    Spacer(Modifier.height(6.dp))

                    Text(video.title, color = Color.White)

                    Spacer(Modifier.height(8.dp))

                    Text(video.caption, color = Color.LightGray)

                    Spacer(Modifier.height(12.dp))

                    // CHIP FIX (IMPORTANT)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        video.tags.take(3).forEach {
                            AssistChip(
                                onClick = {},
                                label = { Text("#$it") }
                            )
                        }
                    }
                }

                // LOADER (DELAYED FEEL)
                if (active && controller.isBuffering) {
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
