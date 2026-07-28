package com.example.streamly.feature.profile.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.streamly.R
import com.example.streamly.ui.theme.StreamlyTheme

@Composable
internal fun ProfileHeader(
    name: String?,
    email: String?,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Background lives on its own outer Box (no chain-order reliance) so it reaches the full
    // extent behind the status bar, while the actual content is inset-padded on a separate inner
    // Box - the same "separate background layer vs. inset content layer" pattern PlayerSurface
    // uses, rather than padding individual children (which drifts out of sync one at a time).
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondary,
                    ),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(bottom = 24.dp),
        ) {
            IconButton(
                onClick = onBackButtonClicked,
                modifier = Modifier.padding(top = 8.dp, start = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.profile_back),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = stringResource(R.string.profile_avatar_description),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(80.dp),
                )
                Text(
                    text = name ?: stringResource(R.string.profile_name_fallback),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = email ?: stringResource(R.string.profile_no_email),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Preview(name = "With profile")
@Composable
private fun ProfileHeaderPreview() {
    StreamlyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProfileHeader(
                name = "Anika Rahman",
                email = "anika@streamly.app",
                onBackButtonClicked = {},
            )
        }
    }
}

@Preview(name = "No profile data")
@Composable
private fun ProfileHeaderFallbackPreview() {
    StreamlyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProfileHeader(
                name = null,
                email = null,
                onBackButtonClicked = {},
            )
        }
    }
}