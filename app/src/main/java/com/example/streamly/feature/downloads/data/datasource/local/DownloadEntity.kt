package com.example.streamly.feature.downloads.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val thumbnailUrl: String?,
    val localUri: String?,
    val status: String,
    val progressPercent: Int,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val createdAtMillis: Long,
)