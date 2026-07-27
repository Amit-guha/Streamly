package com.example.streamly.feature.shorts.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.streamly.R
import com.example.streamly.feature.shorts.presentation.model.ShortUiModel

@Composable
internal fun ShortCaptionOverlay(short: ShortUiModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(R.string.shorts_handle_caption, short.handle, short.caption),
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Text(text = short.description, color = Color.White.copy(alpha = 0.85f))
    }
}