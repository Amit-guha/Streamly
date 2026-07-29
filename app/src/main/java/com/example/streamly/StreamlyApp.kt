package com.example.streamly

import android.app.Application
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadService
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.streamly.core.service.StreamlyDownloadService
import com.example.streamly.feature.downloads.data.download.DownloadNotifier
import com.example.streamly.feature.downloads.data.download.Media3DownloadListener
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class StreamlyApp : Application(), ImageLoaderFactory {

    @Inject
    lateinit var downloadListener: Media3DownloadListener

    @Inject
    lateinit var downloadNotifier: DownloadNotifier

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        downloadListener.start()
        downloadNotifier.start()
        // DownloadManager persists QUEUED/DOWNLOADING state across process death (e.g. the app
        // was force-stopped mid-download), but nothing drives it forward again until the service
        // is (re)started — without this, an interrupted download stays stuck forever.
        DownloadService.start(this, StreamlyDownloadService::class.java)
    }

    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
        .crossfade(true)
        .build()
}