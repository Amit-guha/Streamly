package com.example.streamly.feature.downloads.data.download

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import com.example.streamly.core.service.StreamlyDownloadService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Media3DownloadController @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : DownloadController {


    @UnstableApi
    override suspend fun start(request: DownloadRequest) {
        DownloadService.sendAddDownload(context, StreamlyDownloadService::class.java, request, false)
    }

    @OptIn(UnstableApi::class)
    override suspend fun remove(videoId: String) {
        DownloadService.sendRemoveDownload(context, StreamlyDownloadService::class.java, videoId, false)
    }
}