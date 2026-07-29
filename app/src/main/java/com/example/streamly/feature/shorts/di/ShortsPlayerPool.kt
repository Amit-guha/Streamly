package com.example.streamly.feature.shorts.di

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.streamly.core.common.constant.AppConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Pooled ExoPlayer strategy for the shorts feed: at most [AppConstants.SHORTS_PLAYER_POOL_MAX_SIZE]
 * players are ever alive at once, matching the visible item plus one adjacent item pre-buffering
 * forward — never one player per feed item.
 *
 * Backed by a [LinkedHashMap] used as an LRU cache: [prepare] moves a hit to the most-recently-used
 * (end) position, and a miss evicts+releases the least-recently-used (front) entry once the pool
 * is at capacity. Calling [prepare] for `{currentIndex, currentIndex + 1}` on every page change
 * naturally self-heals to exactly that window in either swipe direction — e.g. swiping back
 * evicts the forward-preloaded player and creates one for the new adjacent index instead.
 */
interface ShortsPlayerPool {
    fun prepare(index: Int, videoUrl: String): Player
    fun currentPlayerOrNull(index: Int): Player?
    fun setMuted(muted: Boolean)
    fun pauseAll()
    fun resume(index: Int)
    fun retryErroredPlayers(currentIndex: Int)
    fun releaseAll()
}

class Media3ShortsPlayerPool @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ShortsPlayerPool {

    private val pooledPlayers = LinkedHashMap<Int, ExoPlayer>()
    private var isMuted = false

    override fun prepare(index: Int, videoUrl: String): Player {
        pooledPlayers.remove(index)?.let { existing ->
            pooledPlayers[index] = existing
            return existing
        }

        if (pooledPlayers.size >= AppConstants.SHORTS_PLAYER_POOL_MAX_SIZE) {
            val leastRecentlyUsedIndex = pooledPlayers.keys.first()
            pooledPlayers.remove(leastRecentlyUsedIndex)?.release()
        }

        val player = ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(AppConstants.SEEK_INCREMENT_MS)
            .setSeekForwardIncrementMs(AppConstants.SEEK_INCREMENT_MS)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
                volume = if (isMuted) 0f else 1f
                setMediaItem(MediaItem.fromUri(videoUrl))
                prepare()
            }
        pooledPlayers[index] = player
        return player
    }

    override fun currentPlayerOrNull(index: Int): Player? = pooledPlayers[index]

    override fun setMuted(muted: Boolean) {
        isMuted = muted
        pooledPlayers.values.forEach { it.volume = if (muted) 0f else 1f }
    }

    override fun pauseAll() {
        pooledPlayers.values.forEach { it.playWhenReady = false }
    }

    override fun resume(index: Int) {
        pooledPlayers[index]?.playWhenReady = true
    }

    // A load failure (e.g. no internet) exhausts ExoPlayer's own retry/backoff and leaves the
    // player parked in STATE_IDLE with a playerError — it does not recover on its own once
    // connectivity returns. The MediaItem is still set, so prepare() is enough to reload it, but
    // prepare() alone doesn't resume playback — that's what re-asserting playWhenReady does,
    // matching what tapping the native play/pause button does under the hood. Only the current
    // index is forced to play; the pre-buffered next one stays paused like it normally would,
    // so a failed pre-buffer doesn't jump straight to auto-playing a short the user hasn't
    // swiped to yet.
    override fun retryErroredPlayers(currentIndex: Int) {
        pooledPlayers.forEach { (index, player) ->
            if (player.playerError != null) {
                player.prepare()
                player.playWhenReady = index == currentIndex
            }
        }
    }

    override fun releaseAll() {
        pooledPlayers.values.forEach { it.release() }
        pooledPlayers.clear()
    }
}