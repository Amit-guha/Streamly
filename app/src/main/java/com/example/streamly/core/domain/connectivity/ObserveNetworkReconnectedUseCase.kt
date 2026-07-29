package com.example.streamly.core.domain.connectivity

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/** Emits once each time connectivity transitions from offline back to online — not on every
 * connectivity change, and not for the initial status observed at subscription time. Shared
 * across features (Player, Shorts) that each need to retry a stalled playback error on reconnect. */
class ObserveNetworkReconnectedUseCase @Inject constructor(
    private val networkMonitor: NetworkMonitor,
) {
    operator fun invoke(): Flow<Unit> = networkMonitor.isOnline
        .distinctUntilChanged()
        .drop(1)
        .filter { isOnline -> isOnline }
        .map { }
}