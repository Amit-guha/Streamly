package com.example.streamly.core.common.constant

import kotlin.time.Duration.Companion.milliseconds

object AppConstants {
    val SPLASH_MIN_DISPLAY_DURATION = 800.milliseconds
     const val VIDEOS_ASSET_FILE_NAME = "videos.json"
     const val SHORTS_ASSET_FILE_NAME = "shorts.json"

    /** Caps the shorts pager's width on wide layouts (foldables/tablets) instead of stretching
     * the video full-bleed edge to edge. Plain Int here (not [androidx.compose.ui.unit.Dp]) so
     * this framework-free constants file stays free of a Compose import. */
    const val SHORTS_MAX_PAGER_WIDTH_DP = 480
}