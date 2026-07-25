package com.example.streamly.core.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [status] stores a [com.example.streamly.core.common.enum.DownloadStatus] name. Kept as a plain
 * String on the entity (no TypeConverter) — feature data-layer mappers translate it to the enum.
 */
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