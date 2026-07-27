package com.example.streamly.feature.shorts.di

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Pooled ExoPlayer strategy for the shorts feed: at most [MAX_POOL_SIZE] players are ever alive
 * at once, matching the visible item plus one adjacent item pre-buffering forward — never one
 * player per feed item.
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

        if (pooledPlayers.size >= MAX_POOL_SIZE) {
            val leastRecentlyUsedIndex = pooledPlayers.keys.first()
            pooledPlayers.remove(leastRecentlyUsedIndex)?.release()
        }

        val player = ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(SEEK_INCREMENT_MS)
            .setSeekForwardIncrementMs(SEEK_INCREMENT_MS)
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

    override fun releaseAll() {
        pooledPlayers.values.forEach { it.release() }
        pooledPlayers.clear()
    }

    private companion object {
        const val MAX_POOL_SIZE = 2
        const val SEEK_INCREMENT_MS = 5_000L
    }
}