package com.example.streamly.feature.downloads.data.download

import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import com.example.streamly.core.common.base.dispatcher.AppDispatchers
import com.example.streamly.core.common.base.dispatcher.Dispatcher
import com.example.streamly.core.common.constant.DownloadConstants
import com.example.streamly.core.common.enum.DownloadStatus
import com.example.streamly.feature.downloads.data.datasource.local.DownloadDao
import com.example.streamly.feature.downloads.data.datasource.local.DownloadEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Singleton
class Media3DownloadListener @UnstableApi
@Inject constructor(
    private val downloadDao: DownloadDao,
    private val downloadManager: DownloadManager,
    @param:Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : DownloadManager.Listener {

    // limitedParallelism(1) rather than the plain ioDispatcher: every mirrorToRoom call is a
    // read-existing-row-then-upsert against the same table, so a callback firing and a poll tick
    // landing concurrently could commit out of order and clobber each other's write. Serializing
    // this scope guarantees every mirror happens in the order it was observed.
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher.limitedParallelism(1))

    @UnstableApi
    fun start() {
        downloadManager.addListener(this)
        startProgressPolling()
    }

    @UnstableApi
    private fun startProgressPolling() {
        scope.launch {
            while (true) {
                downloadManager.currentDownloads.forEach { mirrorToRoom(it) }
                delay(DownloadConstants.PROGRESS_POLL_INTERVAL_MILLIS.milliseconds)
            }
        }
    }

    @UnstableApi
    override fun onDownloadChanged(downloadManager: DownloadManager, download: Download, finalException: Exception?) {
        scope.launch { mirrorToRoom(download) }
    }

    @UnstableApi
    override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
        scope.launch { downloadDao.deleteByVideoId(download.request.id) }
    }

    @UnstableApi
    private suspend fun mirrorToRoom(download: Download) {
        val videoId = download.request.id
        val existing = downloadDao.getByVideoId(videoId)
        val isCompleted = download.state == Download.STATE_COMPLETED
        downloadDao.insert(
            DownloadEntity(
                videoId = videoId,
                title = existing?.title ?: download.request.data.decodeToString(),
                thumbnailUrl = existing?.thumbnailUrl,
                localUri = if (isCompleted) download.request.uri.toString() else null,
                status = download.state.toDownloadStatus().name,
                progressPercent = download.percentDownloadedOrZero(),
                downloadedBytes = download.bytesDownloaded,
                totalBytes = download.contentLength,
                createdAtMillis = existing?.createdAtMillis ?: System.currentTimeMillis(),
            ),
        )
    }

}


@UnstableApi
private fun Download.percentDownloadedOrZero(): Int =
    if (percentDownloaded == C.PERCENTAGE_UNSET.toFloat()) 0 else percentDownloaded.roundToInt().coerceIn(0, 100)

@UnstableApi
private fun Int.toDownloadStatus(): DownloadStatus = when (this) {
    Download.STATE_QUEUED, Download.STATE_RESTARTING -> DownloadStatus.PENDING
    Download.STATE_DOWNLOADING -> DownloadStatus.DOWNLOADING
    Download.STATE_STOPPED -> DownloadStatus.PAUSED
    Download.STATE_COMPLETED -> DownloadStatus.COMPLETED
    Download.STATE_FAILED -> DownloadStatus.FAILED
    else -> DownloadStatus.PENDING
}