package com.openreel.app.ui.screens.feed

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.openreel.app.domain.model.Video

val mockVideos = listOf(
    Video(
        id = "1",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny_320x180_10s_1MB.mp4",
        caption = "First vertical reel • OpenReel MVP",
        creator = "@creator1",
        likes = 1240
    ),
    Video(
        id = "2",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream_320x180_10s_1MB.mp4",
        caption = "Swipe up for next • Agent-ready",
        creator = "@creator2",
        likes = 890
    ),
    Video(
        id = "3",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel_320x180_10s_1MB.mp4",
        caption = "Third reel • Feed ranking coming",
        creator = "@creator3",
        likes = 2340
    )
)

@Composable
fun HomeFeedScreen() {
    val pagerState = rememberPagerState(pageCount = { mockVideos.size })
    val context = LocalContext.current
    val players = remember { mutableMapOf<String, ExoPlayer>() }

    LaunchedEffect(Unit) {
        mockVideos.forEach { video ->
            val player = ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(video.videoUrl))
                prepare()
            }
            players[video.id] = player
        }
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1
    ) { page ->
        val video = mockVideos[page]
        val player = players[video.id] ?: return@VerticalPager

        DisposableEffect(pagerState.currentPage) {
            if (pagerState.currentPage == page) {
                player.play()
            } else {
                player.pause()
            }
            onDispose { }
        }

        VideoPlayer(player = player, video = video)
    }
}
