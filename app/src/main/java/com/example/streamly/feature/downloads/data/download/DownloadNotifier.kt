package com.example.streamly.feature.downloads.data.download

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.OptIn
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import com.example.streamly.R
import com.example.streamly.core.common.constant.DownloadConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Posts a one-shot completed/failed notification per video. [DownloadService][androidx.media3.exoplayer.offline.DownloadService]
 * only gets to draw the ongoing foreground notification while a download is active, so a
 * dedicated [DownloadManager.Listener] — the same integration point [Media3DownloadListener] uses
 * to mirror state to Room — is what observes each download reaching a terminal state.
 */
@OptIn(UnstableApi::class)
@Singleton
class DownloadNotifier
@Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val downloadManager: DownloadManager,
) : DownloadManager.Listener {

    private val notificationHelper: DownloadNotificationHelper by lazy {
        DownloadNotificationHelper(context, DownloadConstants.NOTIFICATION_CHANNEL_ID)
    }

    @UnstableApi
    fun start() {
        downloadManager.addListener(this)
    }

    @UnstableApi
    override fun onDownloadChanged(downloadManager: DownloadManager, download: Download, finalException: Exception?) {
        val notification = when (download.state) {
            Download.STATE_COMPLETED -> notificationHelper.buildDownloadCompletedNotification(
                context,
                R.drawable.ic_launcher_foreground,
                null,
                download.request.data.decodeToString(),
            )

            Download.STATE_FAILED -> notificationHelper.buildDownloadFailedNotification(
                context,
                R.drawable.ic_launcher_foreground,
                null,
                download.request.data.decodeToString(),
            )

            else -> return
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(context)
            .notify(download.request.id.hashCode() and NOTIFICATION_ID_MASK, notification)
    }

    private companion object {
        const val NOTIFICATION_ID_MASK = 0x7FFFFFFF
    }
}