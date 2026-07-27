package com.example.streamly.feature.downloads.data.download

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.offline.DownloadHelper
import androidx.media3.exoplayer.offline.DownloadRequest
import com.example.streamly.core.common.base.dispatcher.AppDispatchers
import com.example.streamly.core.common.base.dispatcher.Dispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

@Singleton
class DownloadRequestFactory @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val httpDataSourceFactory: HttpDataSource.Factory,
    @param:Dispatcher(AppDispatchers.MAIN_IMMEDIATE) private val mainImmediateDispatcher: CoroutineDispatcher,
) {

    @androidx.annotation.OptIn(UnstableApi::class)
    suspend fun create(videoId: String, title: String, videoUrl: String): DownloadRequest =
        withContext(mainImmediateDispatcher) {
            suspendCancellableCoroutine { continuation ->
                val mediaItem = MediaItem.Builder()
                    .setUri(videoUrl.toUri())
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .build()
                val trackSelectionParameters = TrackSelectionParameters.Builder(context)
                    .setForceLowestBitrate(true)
                    .build()
                val helper = DownloadHelper.forMediaItem(
                    mediaItem,
                    trackSelectionParameters,
                    DefaultRenderersFactory(context),
                    httpDataSourceFactory,
                )
                helper.prepare(
                    object : DownloadHelper.Callback {
                        override fun onPrepared(helper: DownloadHelper, isAsync: Boolean) {
                            val request = helper.getDownloadRequest(videoId, title.encodeToByteArray())
                            helper.release()
                            continuation.resume(request)
                        }

                        override fun onPrepareError(helper: DownloadHelper, e: IOException) {
                            helper.release()
                            continuation.resumeWithException(e)
                        }
                    },
                )
            }
        }
}