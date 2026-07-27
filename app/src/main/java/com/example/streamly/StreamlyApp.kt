package com.example.streamly

import android.app.Application
import com.example.streamly.feature.downloads.data.download.Media3DownloadListener
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class StreamlyApp : Application() {

    @Inject
    lateinit var downloadListener: Media3DownloadListener

    override fun onCreate() {
        super.onCreate()
        // Must run before any download can be requested: registers the Media3 DownloadManager
        // listener and starts the progress-polling loop (see Media3DownloadListener).
        downloadListener.start()
    }
}