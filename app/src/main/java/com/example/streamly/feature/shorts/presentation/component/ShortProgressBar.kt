package com.example.streamly.feature.shorts.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.indicators.ProgressIndicator
import kotlin.math.roundToInt

private const val PROGRESS_TICK_COUNT = 1_000
private val TRACK_HEIGHT = 2.dp
private val THUMB_DIAMETER = 14.dp

@androidx.annotation.OptIn(markerClass = [UnstableApi::class])
@Composable
internal fun ShortProgressBar(player: Player, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val activeColor = MaterialTheme.colorScheme.error
    val inactiveColor = activeColor.copy(alpha = 0.35f)
    val density = LocalDensity.current

    ProgressIndicator(player = player, totalTickCount = PROGRESS_TICK_COUNT, scope = scope) {
        var widthPx by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = modifier
                .height(THUMB_DIAMETER)
                .onSizeChanged { widthPx = it.width.toFloat() }
                .then(
                    if (changingProgressEnabled) {
                        Modifier.pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = { offset ->
                                    updateCurrentPositionProgress((offset.x / widthPx).coerceIn(0f, 1f))
                                },
                                onHorizontalDrag = { change, _ ->
                                    change.consume()
                                    updateCurrentPositionProgress((change.position.x / widthPx).coerceIn(0f, 1f))
                                },
                            )
                        }
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TRACK_HEIGHT)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(inactiveColor),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(currentPositionProgress)
                    .height(TRACK_HEIGHT)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(activeColor),
            )
            val thumbOffsetPx = with(density) {
                (widthPx * currentPositionProgress - THUMB_DIAMETER.toPx() / 2).roundToInt().coerceAtLeast(0)
            }
            Box(
                modifier = Modifier
                    .offset { IntOffset(thumbOffsetPx, 0) }
                    .size(THUMB_DIAMETER)
                    .background(if (changingProgressEnabled) activeColor else inactiveColor, CircleShape),
            )
        }
    }
}