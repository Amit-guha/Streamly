package com.example.streamly.feature.player.presentation

import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.example.streamly.core.common.base.MVIViewModel
import com.example.streamly.core.common.enum.Status
import com.example.streamly.feature.player.di.PlayerController
import com.example.streamly.feature.player.domain.usecase.GetVideoDetailsUseCase
import com.example.streamly.feature.player.presentation.contract.PlayerEffect
import com.example.streamly.feature.player.presentation.contract.PlayerIntent
import com.example.streamly.feature.player.presentation.contract.PlayerUiState
import com.example.streamly.feature.player.presentation.model.VideoUiModel
import com.example.streamly.feature.player.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val getVideoDetailsUseCase: GetVideoDetailsUseCase,
) : MVIViewModel<PlayerUiState, PlayerIntent, PlayerEffect>(initialState = PlayerUiState()) {

    // Forwarded straight through — only PlayerSurface/PlayerView needs this, for native
    // play/pause/seek/buffering transport chrome. The ViewModel's own logic never reads it;
    // it goes through playerController's methods instead, which is what's actually unit-tested.
    val player: Player get() = playerController.player

    // Survives configuration changes (unlike a local Compose var), so a rotation's
    // ON_PAUSE -> ON_RESUME cycle resumes playback only if it was actually playing before,
    // instead of unconditionally forcing play() on every resume.
    private var wasPlayingBeforeSystemPause = false

    override fun onIntent(intent: PlayerIntent) {
        when (intent) {
            is PlayerIntent.OnScreenStarted -> start(intent.videoId, intent.title, intent.videoUrl)
            PlayerIntent.OnSubscribeClicked -> _state.update { it.copy(isSubscribed = !it.isSubscribed) }
            PlayerIntent.OnLikeClicked -> _state.update { it.copy(isLiked = !it.isLiked) }
            PlayerIntent.OnShareClicked -> sendEffect(
                PlayerEffect.ShareVideo(videoUrl = _state.value.video.videoUrl, title = _state.value.video.title),
            )
            PlayerIntent.OnMuteToggled -> toggleMute()
            is PlayerIntent.OnUpNextVideoClicked -> playVideo(intent.video)
            PlayerIntent.OnRetryClicked -> loadDetails(_state.value.video.id)
            PlayerIntent.OnLifecyclePaused -> pauseForLifecycle()
            PlayerIntent.OnLifecycleResumed -> resumeForLifecycle()
            PlayerIntent.OnBackRequested -> pauseForExit()
        }
    }

    private fun start(videoId: String, title: String, videoUrl: String) {
        if (_state.value.video.id == videoId) return
        _state.update { it.copy(video = it.video.copy(id = videoId, title = title, videoUrl = videoUrl)) }
        playerController.play(videoUrl)
        loadDetails(videoId)
    }

    private fun playVideo(video: VideoUiModel) {
        _state.update { it.copy(video = video, isLiked = false, isSubscribed = false) }
        playerController.play(video.videoUrl)
        loadDetails(video.id)
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

    private fun pauseForExit() {
        playerController.pause()
    }

    override fun onCleared() {
        playerController.release()
    }
}