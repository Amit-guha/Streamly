package com.example.streamly.feature.shorts.presentation

import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.example.streamly.core.common.base.MVIViewModel
import com.example.streamly.core.common.enum.Status
import com.example.streamly.core.domain.connectivity.ObserveNetworkReconnectedUseCase
import com.example.streamly.feature.shorts.di.ShortsPlayerPool
import com.example.streamly.feature.shorts.domain.usecase.GetShortsUseCase
import com.example.streamly.feature.shorts.presentation.contract.ShortsEffect
import com.example.streamly.feature.shorts.presentation.contract.ShortsIntent
import com.example.streamly.feature.shorts.presentation.contract.ShortsUiState
import com.example.streamly.feature.shorts.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ShortsViewModel @Inject constructor(
    private val shortsPlayerPool: ShortsPlayerPool,
    private val getShortsUseCase: GetShortsUseCase,
    private val observeNetworkReconnectedUseCase: ObserveNetworkReconnectedUseCase,
) : MVIViewModel<ShortsUiState, ShortsIntent, ShortsEffect>(initialState = ShortsUiState()) {

    private var wasPlayingBeforeSystemPause = false

    // currentIndex defaults to 0, same as the very first page, so a plain "index == currentIndex"
    // guard in onPageChanged would also block that legitimate first call, not just later replays.
    // This distinguishes "first time reaching this index" from "already processed" instead.
    private var hasStartedFirstPage = false

    init {
        // A short's HLS load can fail outright with no internet; ExoPlayer won't retry on its
        // own once it gives up, so nudge any errored pooled player to retry as soon as
        // connectivity is restored, regardless of which short is on screen at that moment.
        viewModelScope.launch {
            observeNetworkReconnectedUseCase().collect {
                shortsPlayerPool.retryErroredPlayers(currentIndex = _state.value.currentIndex)
            }
        }
    }

    override fun onIntent(intent: ShortsIntent) {
        when (intent) {
            ShortsIntent.OnScreenStarted -> loadShorts()
            is ShortsIntent.OnPageChanged -> onPageChanged(intent.index)
            ShortsIntent.OnMuteToggled -> toggleMute()
            ShortsIntent.OnRetryClicked -> loadShorts()
            ShortsIntent.OnLifecyclePaused -> pauseForLifecycle()
            ShortsIntent.OnLifecycleResumed -> resumeForLifecycle()
            ShortsIntent.OnBackRequested -> shortsPlayerPool.pauseAll()
        }
    }

    fun playerFor(index: Int): Player? = shortsPlayerPool.currentPlayerOrNull(index)

    private fun loadShorts() {
        viewModelScope.launch {
            getShortsUseCase().collect { result ->
                when (result.status) {
                    Status.LOADING -> _state.update { it.copy(isLoading = true, errorMessage = null) }
                    Status.SUCCESS -> _state.update {
                        it.copy(
                            isLoading = false,
                            shorts = result.data.orEmpty().map { short -> short.toUiModel() },
                            errorMessage = null,
                        )
                    }
                    Status.ERROR -> _state.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                    Status.NOTHING -> Unit
                }
            }
        }
    }

    private fun onPageChanged(index: Int) {
        // rememberPagerState() isn't preserved across configuration changes, so its
        // snapshotFlow(pagerState.currentPage) re-subscribes fresh after rotation and immediately
        // replays whatever page we're already on. Without this guard, that replay would be
        // treated as a genuine swipe and force playback back on over a user's manual pause.
        if (hasStartedFirstPage && index == _state.value.currentIndex) return
        val shorts = _state.value.shorts
        val current = shorts.getOrNull(index) ?: return
        hasStartedFirstPage = true

        _state.update { it.copy(currentIndex = index, playerGeneration = it.playerGeneration + 1) }
        shortsPlayerPool.prepare(index, current.videoUrl).playWhenReady = true

        // Pre-buffer only the next item (forward), never both neighbours — that's what keeps
        // the pool capped at two live players instead of three.
        shorts.getOrNull(index + 1)?.let { next ->
            shortsPlayerPool.prepare(index + 1, next.videoUrl).playWhenReady = false
        }
    }

    private fun toggleMute() {
        val isMuted = !_state.value.isMuted
        shortsPlayerPool.setMuted(isMuted)
        _state.update { it.copy(isMuted = isMuted) }
    }

    private fun pauseForLifecycle() {
        wasPlayingBeforeSystemPause = shortsPlayerPool.currentPlayerOrNull(_state.value.currentIndex)?.playWhenReady == true
        shortsPlayerPool.pauseAll()
    }

    private fun resumeForLifecycle() {
        if (wasPlayingBeforeSystemPause) {
            shortsPlayerPool.resume(_state.value.currentIndex)
        }
    }

    override fun onCleared() {
        shortsPlayerPool.releaseAll()
    }
}