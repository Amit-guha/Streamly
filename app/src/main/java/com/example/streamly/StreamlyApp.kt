package com.example.streamly

import android.app.Application
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.streamly.feature.downloads.data.download.Media3DownloadListener
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class StreamlyApp : Application(), ImageLoaderFactory {

    @Inject
    lateinit var downloadListener: Media3DownloadListener

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        downloadListener.start()
    }

    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
        .crossfade(true)
        .build()
}