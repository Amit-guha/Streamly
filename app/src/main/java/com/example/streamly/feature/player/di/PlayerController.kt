package com.example.streamly.feature.player.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.example.streamly.core.common.constant.AppConstants
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

    fun play(videoUrl: String, preferDownloadedRendition: Boolean = false)
    fun resume()
    fun pause()
    fun setMuted(muted: Boolean)
    fun release()
}

@UnstableApi
class Media3PlayerController @OptIn(UnstableApi::class)
@Inject constructor(
    @ApplicationContext context: Context,
    cacheDataSourceFactory: CacheDataSource.Factory,
) : PlayerController {

    private val trackSelector = DefaultTrackSelector(context)

    // Reading through the same [CacheDataSource.Factory] the download pipeline writes to means a
    // fully-downloaded video plays back from local storage automatically, network otherwise —
    // no special-casing needed for "play a downloaded video" vs. "stream a video".
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context)
        .setTrackSelector(trackSelector)
        .setMediaSourceFactory(DefaultMediaSourceFactory(context).setDataSourceFactory(cacheDataSourceFactory))
        .setSeekForwardIncrementMs(AppConstants.SEEK_INCREMENT_MS)
        .setSeekBackIncrementMs(AppConstants.SEEK_INCREMENT_MS)
        .build()

    override val player: Player = exoPlayer

    override val isPlaying: Boolean
        get() = player.playWhenReady

    override fun play(videoUrl: String, preferDownloadedRendition: Boolean) {
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setForceLowestBitrate(preferDownloadedRendition),
        )
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