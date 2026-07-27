package com.example.streamly.feature.downloads.di

import com.example.streamly.core.local.database.StreamlyDatabase
import com.example.streamly.feature.downloads.data.datasource.local.DownloadDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// DownloadDao is feature-owned (see DownloadEntity/DownloadDao's package) even though the
// StreamlyDatabase instance it comes from is provisioned in core/di/DatabaseModule. A plain
// object here — no companion object needed — since this only has @Provides, not @Binds.
@Module
@InstallIn(SingletonComponent::class)
object DownloadsProvidesModule {

    @Provides
    @Singleton
    fun provideDownloadDao(database: StreamlyDatabase): DownloadDao = database.downloadDao()
}
