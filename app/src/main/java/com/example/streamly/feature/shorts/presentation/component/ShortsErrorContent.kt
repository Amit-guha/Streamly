package com.example.streamly.feature.shorts.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.streamly.R

/** Home's [com.example.streamly.feature.home.presentation.component.ErrorContent] assumes a
 * light background; shorts is always full-bleed black, so it gets its own light-on-dark variant
 * instead of forcing a themed color parameter onto the shared one for a single caller. */
@Composable
internal fun ShortsErrorContent(onRetryClicked: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        Text(stringResource(R.string.shorts_error_message), color = Color.White)
        Button(
            onClick = onRetryClicked,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Text(stringResource(R.string.shorts_retry))
        }
    }
}