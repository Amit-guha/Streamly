package com.example.streamly.feature.downloads.presentation

import androidx.lifecycle.viewModelScope
import com.example.streamly.core.common.base.MVIViewModel
import com.example.streamly.core.common.enum.DownloadStatus
import com.example.streamly.feature.downloads.domain.model.DownloadItem
import com.example.streamly.feature.downloads.domain.usecase.DeleteDownloadUseCase
import com.example.streamly.feature.downloads.domain.usecase.GetDownloadsUseCase
import com.example.streamly.feature.downloads.domain.usecase.GetTotalStorageBytesUseCase
import com.example.streamly.feature.downloads.presentation.contract.DownloadsEffect
import com.example.streamly.feature.downloads.presentation.contract.DownloadsIntent
import com.example.streamly.feature.downloads.presentation.contract.DownloadsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val getDownloadsUseCase: GetDownloadsUseCase,
    private val deleteDownloadUseCase: DeleteDownloadUseCase,
    private val getTotalStorageBytesUseCase: GetTotalStorageBytesUseCase,
) : MVIViewModel<DownloadsUiState, DownloadsIntent, DownloadsEffect>(initialState = DownloadsUiState()) {

    init {
        observeDownloads()
        loadStorageStats()
    }

    override fun onIntent(intent: DownloadsIntent) {
        when (intent) {
            DownloadsIntent.OnScreenStarted -> Unit
            is DownloadsIntent.OnItemClicked -> onItemClicked(intent.download)
            is DownloadsIntent.OnRemoveClicked -> removeDownload(intent.videoId)
            DownloadsIntent.OnBackClicked -> sendEffect(DownloadsEffect.NavigateBack)
        }
    }

    private fun observeDownloads() {
        viewModelScope.launch {
            getDownloadsUseCase().collect { downloads ->
                _state.update {
                    it.copy(
                        downloads = downloads,
                        usedStorageBytes = downloads.sumOf { download -> download.downloadedBytes },
                    )
                }
            }
        }
    }

    private fun loadStorageStats() {
        viewModelScope.launch {
            val totalBytes = getTotalStorageBytesUseCase()
            _state.update { it.copy(totalStorageBytes = totalBytes) }
        }
    }

    private fun onItemClicked(download: DownloadItem) {
        if (download.status == DownloadStatus.COMPLETED) {
            sendEffect(
                DownloadsEffect.NavigateToPlayer(
                    videoId = download.videoId,
                    title = download.title,
                    videoUrl = download.videoUrl,
                ),
            )
        }
    }

    private fun removeDownload(videoId: String) {
        viewModelScope.launch { deleteDownloadUseCase(videoId) }
    }
}