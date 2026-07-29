package com.example.streamly.core.service

import android.app.Notification
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.Scheduler
import androidx.media3.exoplayer.workmanager.WorkManagerScheduler
import com.example.streamly.R
import com.example.streamly.core.common.constant.DownloadConstants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Foreground service that keeps [DownloadManager] alive while downloads run. Media3's
 * [DownloadService] requires [getForegroundNotification] to return *some* [Notification] — that's
 * an Android foreground-service requirement, not something we can opt out of — so this builds a
 * real progress notification via [DownloadNotificationHelper], refreshed every
 * [DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL] while a download is active. Terminal
 * (completed/failed) per-video notifications are handled separately by
 * [com.example.streamly.feature.downloads.data.download.DownloadNotifier], since
 * [DownloadService] itself exposes no per-download callback — only [DownloadManager.Listener]
 * does. Hilt injects [downloadManagerInstance] before [DownloadService]'s own `onCreate()` (which
 * calls [getDownloadManager]) runs, since the Hilt-generated base class injects fields ahead of
 * calling through to it. Its dependencies (the shared [DownloadManager]/`Cache`) are provisioned
 * in [com.example.streamly.core.di.DownloadManagerModule].
 */
@OptIn(UnstableApi::class)
@AndroidEntryPoint
class StreamlyDownloadService : DownloadService(
    DownloadConstants.NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    DownloadConstants.NOTIFICATION_CHANNEL_ID,
    R.string.downloads_notification_channel_name,
    0,
) {

    @Inject
    lateinit var downloadManagerInstance: DownloadManager

    private val notificationHelper: DownloadNotificationHelper by lazy {
        DownloadNotificationHelper(this, DownloadConstants.NOTIFICATION_CHANNEL_ID)
    }

    override fun getDownloadManager(): DownloadManager = downloadManagerInstance

    override fun getScheduler(): Scheduler = WorkManagerScheduler(this, WORK_NAME)

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int,
    ): Notification = notificationHelper.buildProgressNotification(
        this,
        R.drawable.ic_launcher_foreground,
        null,
        progressMessage(downloads),
        downloads,
        notMetRequirements,
    )

    private fun progressMessage(downloads: List<Download>): String? {
        val active = downloads.filter {
            it.state == Download.STATE_DOWNLOADING || it.state == Download.STATE_RESTARTING
        }
        return when (active.size) {
            0 -> null
            1 -> getString(
                R.string.downloads_notification_progress_message,
                active.first().request.data.decodeToString(),
                active.first().percentDownloadedOrZero(),
            )

            else -> getString(R.string.downloads_notification_progress_message_multiple, active.size)
        }
    }

    private companion object {
        const val WORK_NAME = "streamly_download_service_work"
    }
}

@UnstableApi
private fun Download.percentDownloadedOrZero(): Int =
    if (percentDownloaded == C.PERCENTAGE_UNSET.toFloat()) 0 else percentDownloaded.roundToInt().coerceIn(0, 100)