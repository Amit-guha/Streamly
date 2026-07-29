package com.example.streamly.core.common.constant

import kotlin.time.Duration.Companion.milliseconds

object AppConstants {
    val SPLASH_MIN_DISPLAY_DURATION = 1000.milliseconds
     const val VIDEOS_ASSET_FILE_NAME = "videos.json"
     const val SHORTS_ASSET_FILE_NAME = "shorts.json"

    const val SHORTS_MAX_PAGER_WIDTH_DP = 480

    const val SEEK_INCREMENT_MS = 5_000L

    const val GOOGLE_USER_NAME = "Google User"
    const val GOOGLE_USER_EMAIL = "google.user@example.com"

    const val CONNECTIVITY_STOP_TIMEOUT_MILLIS = 5_000L

    const val SHORTS_PLAYER_POOL_MAX_SIZE = 2
}