package com.example.streamly.feature.profile.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.streamly.R
import com.example.streamly.ui.theme.StreamlyTheme

@Composable
internal fun SignOutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        // AlertDialog defaults to surfaceContainerHigh, which Theme.kt doesn't pin identically
        // across light/dark — pin explicitly to the roles that are, matching
        // DownloadOptionsBottomSheet's fix for the same underlying issue.
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        title = { Text(text = stringResource(R.string.profile_sign_out_dialog_title)) },
        text = { Text(text = stringResource(R.string.profile_sign_out_dialog_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.profile_sign_out_dialog_confirm),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.profile_sign_out_dialog_cancel))
            }
        },
    )
}

@Preview(name = "Sign out dialog")
@Composable
private fun SignOutConfirmationDialogPreview() {
    StreamlyTheme {
        SignOutConfirmationDialog(onConfirm = {}, onDismiss = {})
    }
}