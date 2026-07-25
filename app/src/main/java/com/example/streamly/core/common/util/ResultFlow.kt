package com.example.streamly.core.common.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Wraps a suspend call (typically a network request) as a [Flow] of [Result], emitting
 * [Result.loading] first, then [Result.success] or [Result.error]. Repository implementations
 * use this so callers never see a raw exception.
 */
fun <T> resultFlow(block: suspend () -> T): Flow<Result<T>> = flow {
    emit(Result.loading())
    emit(Result.success(block()))
}.catch { throwable ->
    emit(Result.error(throwable as? Exception ?: Exception(throwable)))
}