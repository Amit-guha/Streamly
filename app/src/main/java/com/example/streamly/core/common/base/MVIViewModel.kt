package com.example.streamly.core.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class MVIViewModel<S, I, E>(
    initialState: S,
) : ViewModel() {

    protected val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<E>()
    val effect: SharedFlow<E> = _effect.asSharedFlow()

    protected fun sendEffect(effect: E) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    abstract fun onIntent(intent: I)
}