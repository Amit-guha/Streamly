package com.example.streamly.core.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.DownloadManager
import com.example.streamly.core.common.constant.DownloadConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DownloadManagerModule {

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideDownloadDatabaseProvider(@ApplicationContext context: Context): DatabaseProvider =
        StandaloneDatabaseProvider(context)

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideDownloadCache(
        @ApplicationContext context: Context,
        databaseProvider: DatabaseProvider,
    ): Cache {
        val downloadDirectory = File(
            context.getExternalFilesDir(null) ?: context.filesDir,
            DownloadConstants.DOWNLOAD_CONTENT_DIRECTORY,
        )
        return SimpleCache(downloadDirectory, NoOpCacheEvictor(), databaseProvider)
    }

    @Provides
    @Singleton
    fun provideHttpDataSourceFactory(): HttpDataSource.Factory = DefaultHttpDataSource.Factory()

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideCacheDataSourceFactory(
        cache: Cache,
        httpDataSourceFactory: HttpDataSource.Factory,
    ): CacheDataSource.Factory = CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(httpDataSourceFactory)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    @Provides
    @Singleton
    fun provideDownloadExecutor(): ExecutorService = Executors.newSingleThreadExecutor()

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideDownloadManager(
        @ApplicationContext context: Context,
        databaseProvider: DatabaseProvider,
        cache: Cache,
        httpDataSourceFactory: HttpDataSource.Factory,
        executorService: ExecutorService,
    ): DownloadManager = DownloadManager(
        context,
        databaseProvider,
        cache,
        httpDataSourceFactory,
        executorService,
    ).apply {
        maxParallelDownloads = 3
    }
}