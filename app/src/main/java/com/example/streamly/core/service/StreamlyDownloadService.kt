package com.example.streamly.core.service

import android.app.Notification
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.Scheduler
import androidx.media3.exoplayer.workmanager.WorkManagerScheduler
import com.example.streamly.R
import com.example.streamly.core.common.constant.DownloadConstants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Foreground service that keeps [DownloadManager] alive while downloads run. Media3's
 * [DownloadService] requires [getForegroundNotification] to return *some* [Notification] — that's
 * an Android foreground-service requirement, not something we can opt out of — so this builds the
 * simplest notification that satisfies it, with no visible UI polish (no progress bar/text). Hilt
 * injects [downloadManagerInstance] before [DownloadService]'s own `onCreate()` (which calls
 * [getDownloadManager]) runs, since the Hilt-generated base class injects fields ahead of calling
 * through to it. Its dependencies (the shared [DownloadManager]/`Cache`) are provisioned in
 * [com.example.streamly.core.di.DownloadManagerModule].
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

    override fun getDownloadManager(): DownloadManager = downloadManagerInstance

    override fun getScheduler(): Scheduler = WorkManagerScheduler(this, WORK_NAME)

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int,
    ): Notification = NotificationCompat.Builder(this, DownloadConstants.NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setOngoing(true)
        .build()

    private companion object {
        const val WORK_NAME = "streamly_download_service_work"
    }
}
