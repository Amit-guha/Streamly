package com.example.streamly.feature.player.presentation

import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.example.streamly.core.common.base.MVIViewModel
import com.example.streamly.core.common.enum.DownloadStatus
import com.example.streamly.core.common.enum.Status
import com.example.streamly.feature.downloads.domain.usecase.DeleteDownloadUseCase
import com.example.streamly.feature.downloads.domain.usecase.DownloadVideoUseCase
import com.example.streamly.feature.downloads.domain.usecase.GetDownloadStatusUseCase
import com.example.streamly.feature.player.di.PlayerController
import com.example.streamly.feature.player.domain.usecase.GetVideoDetailsUseCase
import com.example.streamly.feature.player.presentation.contract.PlayerEffect
import com.example.streamly.feature.player.presentation.contract.PlayerIntent
import com.example.streamly.feature.player.presentation.contract.PlayerUiState
import com.example.streamly.feature.player.presentation.model.VideoUiModel
import com.example.streamly.feature.player.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val getVideoDetailsUseCase: GetVideoDetailsUseCase,
    private val downloadVideoUseCase: DownloadVideoUseCase,
    private val deleteDownloadUseCase: DeleteDownloadUseCase,
    private val getDownloadStatusUseCase: GetDownloadStatusUseCase,
) : MVIViewModel<PlayerUiState, PlayerIntent, PlayerEffect>(initialState = PlayerUiState()) {

    // Forwarded straight through — only PlayerSurface/PlayerView needs this, for native
    // play/pause/seek/buffering transport chrome. The ViewModel's own logic never reads it;
    // it goes through playerController's methods instead, which is what's actually unit-tested.
    val player: Player get() = playerController.player

    private var wasPlayingBeforeSystemPause = false
    private var downloadStatusJob: Job? = null
    private var hasStarted = false

    override fun onIntent(intent: PlayerIntent) {
        when (intent) {
            is PlayerIntent.OnScreenStarted -> start(intent.videoId, intent.title, intent.videoUrl)
            PlayerIntent.OnSubscribeClicked -> _state.update { it.copy(isSubscribed = !it.isSubscribed) }
            PlayerIntent.OnLikeClicked -> _state.update { it.copy(isLiked = !it.isLiked) }
            PlayerIntent.OnShareClicked -> sendEffect(
                PlayerEffect.ShareVideo(videoUrl = _state.value.video.videoUrl, title = _state.value.video.title),
            )
            PlayerIntent.OnDownloadClicked -> onDownloadIconClicked()
            PlayerIntent.OnRemoveDownloadClicked -> removeCurrentDownload()
            PlayerIntent.OnDownloadSheetDismissed -> _state.update { it.copy(showDownloadOptionsSheet = false) }
            PlayerIntent.OnMuteToggled -> toggleMute()
            is PlayerIntent.OnUpNextVideoClicked -> playVideo(intent.video)
            PlayerIntent.OnRetryClicked -> loadDetails(_state.value.video.id)
            PlayerIntent.OnLifecyclePaused -> pauseForLifecycle()
            PlayerIntent.OnLifecycleResumed -> resumeForLifecycle()
            PlayerIntent.OnBackRequested -> releaseForExit()
        }
    }

    private fun start(videoId: String, title: String, videoUrl: String) {
        if (hasStarted) return
        hasStarted = true
        _state.update { it.copy(video = it.video.copy(id = videoId, title = title, videoUrl = videoUrl)) }
        playCurrentVideo(videoId, videoUrl)
        loadDetails(videoId)
        observeDownloadStatus(videoId)
    }

    private fun playVideo(video: VideoUiModel) {
        _state.update { it.copy(video = video, isLiked = false, isSubscribed = false) }
        playCurrentVideo(video.id, video.videoUrl)
        loadDetails(video.id)
        observeDownloadStatus(video.id)
    }


    private fun playCurrentVideo(videoId: String, videoUrl: String) {
        viewModelScope.launch {
            val isDownloaded = getDownloadStatusUseCase(videoId).first()?.status == DownloadStatus.COMPLETED
            playerController.play(videoUrl, preferDownloadedRendition = isDownloaded)
        }
    }

    private fun observeDownloadStatus(videoId: String) {
        downloadStatusJob?.cancel()
        downloadStatusJob = viewModelScope.launch {
            getDownloadStatusUseCase(videoId).collect { download ->
                _state.update { it.copy(downloadStatus = download?.status) }
            }
        }
    }

    // Not downloaded yet (or a previous attempt failed) -> start one. Any other status means a
    // download already exists in some form, so the click opens the "manage this download" sheet
    // instead of restarting it.
    private fun onDownloadIconClicked() {
        val status = _state.value.downloadStatus
        if (status == null || status == DownloadStatus.FAILED) {
            downloadCurrentVideo()
        } else {
            _state.update { it.copy(showDownloadOptionsSheet = true) }
        }
    }

    private fun downloadCurrentVideo() {
        val video = _state.value.video
        viewModelScope.launch {
            downloadVideoUseCase(
                videoId = video.id,
                title = video.title,
                thumbnailUrl = video.thumbnailUrl,
                videoUrl = video.videoUrl,
            )
        }
        sendEffect(PlayerEffect.ShowDownloadStartedSnackbar)
    }

    private fun removeCurrentDownload() {
        val videoId = _state.value.video.id
        viewModelScope.launch { deleteDownloadUseCase(videoId) }
        _state.update { it.copy(showDownloadOptionsSheet = false) }
    }

    private fun loadDetails(videoId: String) {
        viewModelScope.launch {
            getVideoDetailsUseCase(videoId).collect { result ->
                when (result.status) {
                    Status.LOADING -> _state.update { it.copy(isLoadingUpNext = true, upNextErrorMessage = null) }
                    Status.SUCCESS -> _state.update { current ->
                        result.data?.let { details ->
                            current.copy(
                                video = details.video.toUiModel(),
                                upNext = details.upNext.map { it.toUiModel() },
                                isLoadingUpNext = false,
                            )
                        } ?: current.copy(isLoadingUpNext = false)
                    }
                    Status.ERROR -> _state.update {
                        it.copy(isLoadingUpNext = false, upNextErrorMessage = result.message)
                    }
                    Status.NOTHING -> Unit
                }
            }
        }
    }

    private fun toggleMute() {
        val isMuted = !_state.value.isMuted
        playerController.setMuted(isMuted)
        _state.update { it.copy(isMuted = isMuted) }
    }

    private fun pauseForLifecycle() {
        wasPlayingBeforeSystemPause = playerController.isPlaying
        playerController.pause()
    }

    private fun resumeForLifecycle() {
        if (wasPlayingBeforeSystemPause) {
            playerController.resume()
        }
    }

    private fun releaseForExit() {
        playerController.release()
    }

    override fun onCleared() {
        playerController.release()
    }
}