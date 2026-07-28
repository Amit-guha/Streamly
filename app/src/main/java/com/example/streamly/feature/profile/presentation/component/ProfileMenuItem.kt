package com.example.streamly.feature.profile.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.streamly.ui.theme.StreamlyTheme

@Composable
internal fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (enabled) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = contentColor)
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = contentColor)
    }
}

@Preview(name = "Enabled")
@Composable
private fun ProfileMenuItemEnabledPreview() {
    StreamlyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProfileMenuItem(
                icon = Icons.Filled.Download,
                label = "Downloads",
                enabled = true,
                onClick = {},
            )
        }
    }
}

@Preview(name = "Disabled")
@Composable
private fun ProfileMenuItemDisabledPreview() {
    StreamlyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProfileMenuItem(
                icon = Icons.Filled.Download,
                label = "Settings",
                enabled = false,
                onClick = {},
            )
        }
    }
}