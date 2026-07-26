package com.example.streamly.feature.player.di

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Wraps the raw Media3 [Player] so [com.example.streamly.feature.player.presentation.PlayerViewModel]
 * depends on a small, fakeable contract instead of Media3's full [Player] surface. [player] itself
 * is still exposed — PlayerSurface/PlayerView needs the live reference for native transport
 * chrome (play/pause/seek/buffering UI) — but the ViewModel's own decisions (what to play, mute,
 * pause/resume) go through these methods, which are trivial to fake in ViewModel unit tests.
 */
interface PlayerController {
    val player: Player
    val isPlaying: Boolean

    fun play(videoUrl: String)
    fun resume()
    fun pause()
    fun setMuted(muted: Boolean)
    fun release()
}

class Media3PlayerController @Inject constructor(
    @ApplicationContext context: Context,
) : PlayerController {

    override val player: Player = ExoPlayer.Builder(context).build()

    override val isPlaying: Boolean
        get() = player.playWhenReady

    override fun play(videoUrl: String) {
        player.setMediaItem(MediaItem.fromUri(videoUrl))
        player.prepare()
        player.playWhenReady = true
    }

    override fun resume() {
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun setMuted(muted: Boolean) {
        player.volume = if (muted) 0f else 1f
    }

    override fun release() {
        player.release()
    }
}