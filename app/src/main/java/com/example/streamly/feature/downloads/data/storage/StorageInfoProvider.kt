package com.example.streamly.feature.downloads.data.storage

interface StorageInfoProvider {
    suspend fun totalStorageBytes(): Long
}
