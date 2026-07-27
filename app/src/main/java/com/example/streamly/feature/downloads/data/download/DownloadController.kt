package com.example.streamly.feature.downloads.data.download

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadRequest

interface DownloadController {

    @UnstableApi
    suspend fun start(request: DownloadRequest)
    suspend fun remove(videoId: String)
}