package com.example.streamly.feature.downloads.data.storage

import android.content.Context
import android.os.StatFs
import com.example.streamly.core.common.base.dispatcher.AppDispatchers
import com.example.streamly.core.common.base.dispatcher.Dispatcher
import com.example.streamly.core.common.constant.DownloadConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Singleton
class AndroidStorageInfoProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : StorageInfoProvider {

    override suspend fun totalStorageBytes(): Long = withContext(ioDispatcher) {
        val downloadDirectory = File(
            context.getExternalFilesDir(null) ?: context.filesDir,
            DownloadConstants.DOWNLOAD_CONTENT_DIRECTORY,
        )
        val statsDirectory = if (downloadDirectory.exists()) downloadDirectory else downloadDirectory.parentFile
        val statFs = StatFs((statsDirectory ?: downloadDirectory).path)
        statFs.blockSizeLong * statFs.blockCountLong
    }
}
