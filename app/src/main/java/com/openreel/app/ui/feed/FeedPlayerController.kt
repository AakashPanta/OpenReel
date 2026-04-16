package com.openreel.app.ui.feed

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.openreel.app.data.model.VideoPost

class FeedPlayerController(context: Context) {

    private val appContext = context.applicationContext
    private var released = false

    val player: ExoPlayer = ExoPlayer.Builder(appContext).build().apply {
        repeatMode = Player.REPEAT_MODE_ONE
        playWhenReady = true
        volume = 0f
    }

    private val preloadPlayer: ExoPlayer = ExoPlayer.Builder(appContext).build().apply {
        repeatMode = Player.REPEAT_MODE_OFF
        playWhenReady = false
        volume = 0f
    }

    var activeVideoId by mutableStateOf<String?>(null)
        private set

    private var activeVideoUrl: String? = null
    private var preloadedVideoUrl: String? = null

    var isMuted by mutableStateOf(true)
        private set

    var isBuffering by mutableStateOf(false)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var progress by mutableFloatStateOf(0f)
        private set

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            isBuffering = playbackState == Player.STATE_BUFFERING
            if (playbackState == Player.STATE_ENDED) {
                progress = 1f
            } else {
                updateProgress()
            }
        }

        override fun onIsPlayingChanged(isPlayingNow: Boolean) {
            isPlaying = isPlayingNow
        }
    }

    init {
        player.addListener(listener)
    }

    fun play(video: VideoPost) {
        if (released) return

        if (activeVideoId == video.id && activeVideoUrl == video.videoUrl) {
            if (!player.playWhenReady) player.playWhenReady = true
            return
        }

        activeVideoId = video.id
        activeVideoUrl = video.videoUrl
        isBuffering = true
        progress = 0f

        player.setMediaItem(MediaItem.fromUri(video.videoUrl))
        player.prepare()
        player.playWhenReady = true
    }

    fun preload(video: VideoPost?) {
        if (released) return

        if (video == null) {
            preloadedVideoUrl = null
            preloadPlayer.stop()
            preloadPlayer.clearMediaItems()
            return
        }

        if (preloadedVideoUrl == video.videoUrl) return

        preloadedVideoUrl = video.videoUrl
        preloadPlayer.setMediaItem(MediaItem.fromUri(video.videoUrl))
        preloadPlayer.prepare()
        preloadPlayer.playWhenReady = false
    }

    fun pause() {
        if (released) return
        player.playWhenReady = false
    }

    fun resume() {
        if (released) return
        if (activeVideoId != null) {
            player.playWhenReady = true
        }
    }

    fun toggleMute() {
        if (released) return
        isMuted = !isMuted
        player.volume = if (isMuted) 0f else 1f
    }

    fun togglePlayPause() {
        if (released) return
        player.playWhenReady = !player.playWhenReady
    }

    fun updateProgress() {
        if (released) return

        val duration = player.duration
        val position = player.currentPosition.coerceAtLeast(0L)

        progress = if (duration != C.TIME_UNSET && duration > 0) {
            (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    fun release() {
        if (released) return
        released = true
        player.removeListener(listener)
        player.release()
        preloadPlayer.release()
    }
}
