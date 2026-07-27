package com.example.streamly.feature.home.presentation

import androidx.lifecycle.viewModelScope
import com.example.streamly.core.common.base.MVIViewModel
import com.example.streamly.core.common.enum.Status
import com.example.streamly.feature.home.domain.usecase.GetHomeFeedUseCase
import com.example.streamly.feature.home.presentation.contract.HomeEffect
import com.example.streamly.feature.home.presentation.contract.HomeIntent
import com.example.streamly.feature.home.presentation.contract.HomeUiState
import com.example.streamly.feature.home.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeFeedUseCase: GetHomeFeedUseCase,
) : MVIViewModel<HomeUiState, HomeIntent, HomeEffect>(initialState = HomeUiState()) {

    init {
        loadFeed()
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OnVideoThumbnailClicked -> sendEffect(HomeEffect.NavigateToPlayer(intent.video))
            HomeIntent.OnRetryClicked -> loadFeed()
            HomeIntent.OnShortsClicked -> sendEffect(HomeEffect.NavigateToShorts)
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            getHomeFeedUseCase().collect { result ->
                when (result.status) {
                    Status.LOADING -> _state.update { it.copy(isLoading = true, errorMessage = null) }
                    Status.SUCCESS -> _state.update {
                        it.copy(
                            isLoading = false,
                            videos = result.data.orEmpty().map { video -> video.toUiModel() },
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
}