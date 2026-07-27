package com.example.streamly.feature.downloads.di

import com.example.streamly.feature.downloads.data.download.DownloadController
import com.example.streamly.feature.downloads.data.download.Media3DownloadController
import com.example.streamly.feature.downloads.data.repository.DownloadsRepositoryImpl
import com.example.streamly.feature.downloads.data.storage.AndroidStorageInfoProvider
import com.example.streamly.feature.downloads.data.storage.StorageInfoProvider
import com.example.streamly.feature.downloads.domain.repository.DownloadsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DownloadsModule {

    @Binds
    @Singleton
    abstract fun bindDownloadsRepository(impl: DownloadsRepositoryImpl): DownloadsRepository

    @Binds
    @Singleton
    abstract fun bindDownloadController(impl: Media3DownloadController): DownloadController

    @Binds
    @Singleton
    abstract fun bindStorageInfoProvider(impl: AndroidStorageInfoProvider): StorageInfoProvider
}
