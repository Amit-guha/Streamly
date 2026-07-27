package com.example.streamly.core.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.streamly.feature.downloads.data.datasource.local.DownloadDao
import com.example.streamly.feature.downloads.data.datasource.local.DownloadEntity

@Database(
    entities = [DownloadEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class StreamlyDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
}